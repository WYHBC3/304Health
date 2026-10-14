package com.fit5046.health304.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** The latest personalised suggestion for a user (one row per user, replaced each time). */
@Entity(tableName = "recommendations")
data class Recommendation(
    @PrimaryKey
    val userId: String,
    val title: String,
    val message: String,
    val category: String,
    val reason: String,     // which context data led to this suggestion
    val createdAt: Long = System.currentTimeMillis()
)