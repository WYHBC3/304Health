package com.fit5046.health304.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Clears account-scoped subscriptions when Firebase signs out or changes account. */
abstract class AccountDataViewModel(application: Application) : AndroidViewModel(application) {
    private val auth = FirebaseAuth.getInstance()
    private val activeUid = MutableStateFlow(auth.currentUser?.uid)
    val userId = activeUid.asStateFlow()
    private val listener = FirebaseAuth.AuthStateListener { activeUid.value = it.currentUser?.uid }

    init { auth.addAuthStateListener(listener) }

    protected fun requireUserId(): String =
        auth.currentUser?.uid ?: error("Please sign in before saving personal data.")

    override fun onCleared() {
        auth.removeAuthStateListener(listener)
        super.onCleared()
    }
}
