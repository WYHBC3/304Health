package com.fit5046.health304.repository

import android.app.Application
import com.fit5046.health304.data.AppDatabase
import com.fit5046.health304.data.User
import com.fit5046.health304.data.UserDAO
import kotlinx.coroutines.flow.Flow

class UserRepository(application: Application) {

    private val userDao: UserDAO =
        AppDatabase.getDatabase(application).userDAO()

    fun getUser(uid: String): Flow<User?> = userDao.getUser(uid)

    suspend fun insert(user: User) {
        userDao.insertUser(user)
    }

    suspend fun update(user: User) {
        userDao.updateUser(user)
    }

    suspend fun delete(user: User) {
        userDao.deleteUser(user)
    }
}