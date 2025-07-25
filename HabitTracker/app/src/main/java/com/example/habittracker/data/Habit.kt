package com.example.habittracker.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Habit(
    val id: String = "",
    val goalId: String = "",
    val name: String = "",
    val description: String = "",
    val currentConfidence: String = "",
    val targetLevel: String = "",
    val feelings: String = "",
    val targetCount: Int = 20,
    var happyMonkeyCount: Int = 1,
    @ServerTimestamp
    val createdAt: Date = Date()
)
