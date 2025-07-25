package com.example.habittracker.db

import com.example.habittracker.data.Goal
import com.example.habittracker.data.Habit
import com.example.habittracker.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUser() = auth.currentUser

    suspend fun getUser(uid: String): User? {
        return try {
            firestore.collection("users").document(uid).get().await().toObject<User>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveUser(user: User) {
        firestore.collection("users").document(user.uid).set(user).await()
    }

    suspend fun getGoals(): List<Goal> {
        return try {
            firestore.collection("users").document(getCurrentUser()!!.uid).collection("goals").get().await().map { it.toObject<Goal>() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveGoal(goal: Goal) {
        firestore.collection("users").document(getCurrentUser()!!.uid).collection("goals").document(goal.id).set(goal).await()
    }

    suspend fun deleteGoal(goalId: String) {
        firestore.collection("users").document(getCurrentUser()!!.uid).collection("goals").document(goalId).delete().await()
    }

    suspend fun getHabits(goalId: String): List<Habit> {
        return try {
            firestore.collection("users").document(getCurrentUser()!!.uid).collection("goals").document(goalId).collection("habits").get().await().map { it.toObject<Habit>() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveHabit(habit: Habit) {
        firestore.collection("users").document(getCurrentUser()!!.uid).collection("goals").document(habit.goalId).collection("habits").document(habit.id).set(habit).await()
    }

    suspend fun deleteHabit(goalId: String, habitId: String) {
        firestore.collection("users").document(getCurrentUser()!!.uid).collection("goals").document(goalId).collection("habits").document(habitId).delete().await()
    }
}
