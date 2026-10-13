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

                // If this account was created on another device, Room has no row yet
                if (userRepository.getUser(firebaseUser.uid).first() == null) {
                    val names = (firebaseUser.displayName ?: "").split(" ", limit = 2)
                    userRepository.insert(
                        User(
                            uid = firebaseUser.uid,
                            firstName = names.getOrElse(0) { "" },
                            lastName = names.getOrElse(1) { "" },
                            email = firebaseUser.email ?: email
                        )
                    )
                }
                Log.d(TAG, "Login success: uid=${firebaseUser.uid}")
                onSuccess()
            } catch (e: Exception) {
                Log.w(TAG, "Login failed", e)
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
        authRepository.logout()
        Log.d(TAG, "User logged out")
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
