package com.example.habitforge.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,

    // 🔥 NEW FIELDS FOR STREAKS
    val streak: Int = 0,
    val lastCompletedDate: String? = null
)
