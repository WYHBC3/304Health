package com.fit5046.health304.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

/**
 * Wraps Firebase Authentication so the ViewModel does not talk to Firebase directly.
 * Firebase returns Task objects; await() turns them into suspend functions.
 */
class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Firebase keeps the session on the device, so this is non-null after an app restart
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun register(email: String, password: String): FirebaseUser {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user ?: throw IllegalStateException("Registration failed")
    }

    suspend fun updateDisplayName(user: FirebaseUser, displayName: String) {
        val request = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()
        user.updateProfile(request).await()
    }

    suspend fun login(email: String, password: String): FirebaseUser {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user ?: throw IllegalStateException("Login failed")
    }

    suspend fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    fun logout() {
        auth.signOut()
    }
}