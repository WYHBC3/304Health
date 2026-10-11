package com.fit5046.health304.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDAO {

    // Flow emits a new value every time this user's row changes
    @Query("SELECT * FROM users WHERE uid = :uid")
    fun getUser(uid: String): Flow<User?>

    // REPLACE: signing in again with the same account overwrites the old row
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)
}