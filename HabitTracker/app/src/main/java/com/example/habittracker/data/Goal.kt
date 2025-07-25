package com.example.habittracker.data

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Goal(
    val id: String = "",
    val name: String = "",
    val currentConfidence: String = "",
    val targetLevel: String = "",
    val feelings: String = "",
    var score: Int = 1,
    @ServerTimestamp
    val createdAt: Date = Date()
)
