package com.fit5046.health304.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fit5046.health304.data.User
import com.fit5046.health304.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * AndroidViewModel (not plain ViewModel) because the Repository needs
 * the Application to create the Room database.
 */
class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val uRepository: UserRepository = UserRepository(application)

    fun getUser(uid: String): Flow<User?> = uRepository.getUser(uid)

    // Database writes run on a background thread so the UI is never blocked
    fun insertUser(user: User) = viewModelScope.launch(Dispatchers.IO) {
        uRepository.insert(user)
    }

    fun updateUser(user: User) = viewModelScope.launch(Dispatchers.IO) {
        uRepository.update(user)
    }

    fun deleteUser(user: User) = viewModelScope.launch(Dispatchers.IO) {
        uRepository.delete(user)
    }
}