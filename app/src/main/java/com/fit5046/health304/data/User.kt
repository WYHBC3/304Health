package com.fit5046.health304.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Local copy of a registered user's profile.
 * The primary key is the Firebase Authentication UID, so each Firebase account
 * maps to exactly one row in this table.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val uid: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val createdAt: Long = System.currentTimeMillis()
)