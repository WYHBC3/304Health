package com.fit5046.health304.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fit5046.health304.data.User
import com.fit5046.health304.repository.AuthRepository
import com.fit5046.health304.repository.UserRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.fit5046.health304.R
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseUser
import com.fit5046.health304.worker.WorkScheduler

/**
 * Owns the state of the Login and Register screens.
 * Uses Firebase Authentication for accounts and Room (UserRepository) for the local profile.
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository()
    private val userRepository = UserRepository(application)

    // Screen state (same pattern as the Retrofit lab): readable by the UI, only changed here
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    val isLoggedIn: Boolean
        get() = authRepository.currentUser != null

    fun clearError() {
        errorMessage = null
    }

    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // 1. Create the account in Firebase (this also signs the user in)
                val firebaseUser = authRepository.register(email, password)
                authRepository.updateDisplayName(firebaseUser, "$firstName $lastName")

                // 2. Save a local copy of the profile in Room, keyed by the Firebase UID
                userRepository.insert(
                    User(
                        uid = firebaseUser.uid,
                        firstName = firstName,
                        lastName = lastName,
                        email = email
                    )
                )
                Log.d(TAG, "Registered and saved to Room: uid=${firebaseUser.uid}")
                WorkScheduler.start(getApplication())
                onSuccess()
            } catch (e: Exception) {
                Log.w(TAG, "Register failed", e)
                errorMessage = toFriendlyMessage(e)
            } finally {
                isLoading = false
            }
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val firebaseUser = authRepository.login(email, password)

                ensureLocalUser(firebaseUser, email)
                Log.d(TAG, "Login success: uid=${firebaseUser.uid}")
                WorkScheduler.start(getApplication())
                onSuccess()
            } catch (e: Exception) {
                Log.w(TAG, "Login failed", e)
                errorMessage = toFriendlyMessage(e)
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Google sign-in in two steps:
     * 1. Credential Manager shows the Google account picker and returns an ID token
     * 2. The ID token is exchanged for a Firebase account (AuthRepository)
     *
     * `context` must be the Activity, because the account picker is shown on top of it.
     */
    fun signInWithGoogle(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val googleOption = GetSignInWithGoogleOption.Builder(
                    serverClientId = context.getString(R.string.default_web_client_id)
                ).build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleOption)
                    .build()

                val result = CredentialManager.create(context).getCredential(context, request)
                val credential = result.credential

                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val firebaseUser = authRepository.loginWithGoogle(googleCredential.idToken)
                    ensureLocalUser(firebaseUser, googleCredential.id)
                    Log.d(TAG, "Google sign-in success: uid=${firebaseUser.uid}")
                    WorkScheduler.start(getApplication())
                    onSuccess()
                } else {
                    errorMessage = "Unexpected sign-in response. Please try again."
                }
            } catch (e: GetCredentialCancellationException) {
                // The user closed the account picker: not an error, so show nothing
                Log.d(TAG, "Google sign-in cancelled by user")
            } catch (e: NoCredentialException) {
                errorMessage = "No Google account found on this device. Add one in Settings and try again."
            } catch (e: Exception) {
                Log.w(TAG, "Google sign-in failed", e)
                errorMessage = toFriendlyMessage(e)
            } finally {
                isLoading = false
            }
        }
    }

    fun sendPasswordReset(email: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            try {
                authRepository.sendPasswordReset(email)
                // Firebase does not reveal whether the email is registered (privacy protection)
                onResult("If an account exists for $email, a reset link has been sent.")
            } catch (e: Exception) {
                Log.w(TAG, "Password reset failed", e)
                onResult(toFriendlyMessage(e))
            }
        }
    }

    fun logout() {
        WorkScheduler.stop(getApplication())
        authRepository.logout()
        Log.d(TAG, "User logged out")
    }

    // Make sure Room has a profile row for this Firebase account
    private suspend fun ensureLocalUser(firebaseUser: FirebaseUser, fallbackEmail: String) {
        if (userRepository.getUser(firebaseUser.uid).first() == null) {
            val names = (firebaseUser.displayName ?: "").split(" ", limit = 2)
            userRepository.insert(
                User(
                    uid = firebaseUser.uid,
                    firstName = names.getOrElse(0) { "" },
                    lastName = names.getOrElse(1) { "" },
                    email = firebaseUser.email ?: fallbackEmail
                )
            )
            Log.d(TAG, "Local profile created in Room for uid=${firebaseUser.uid}")
        }
    }

    // Turn Firebase exceptions into messages a normal user can understand
    private fun toFriendlyMessage(e: Exception): String = when (e) {
        is FirebaseAuthWeakPasswordException ->
            "Password is too weak. Use at least 8 characters including a number."
        is FirebaseAuthUserCollisionException ->
            "An account with this email already exists. Try logging in instead."
        is FirebaseAuthInvalidUserException ->
            "This account does not exist or has been disabled."
        is FirebaseAuthInvalidCredentialsException ->
            "Incorrect email or password. Please try again."
        is FirebaseTooManyRequestsException ->
            "Too many attempts. Please wait a moment and try again."
        is FirebaseNetworkException ->
            "No internet connection. Please check your network and try again."
        else -> "Something went wrong. Please try again."
    }

    companion object {
        private const val TAG = "AuthViewModel"
    }
}
