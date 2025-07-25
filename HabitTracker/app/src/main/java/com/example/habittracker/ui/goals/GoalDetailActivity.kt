package com.example.habittracker.ui.goals

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittracker.AddHabitActivity
import com.example.habittracker.databinding.ActivityGoalDetailBinding
import com.example.habittracker.db.FirestoreRepository
import com.example.habittracker.ui.habits.HabitAdapter
import kotlinx.coroutines.launch

class GoalDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalDetailBinding
    private val firestoreRepository = FirestoreRepository()
    private lateinit var goalId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goalId = intent.getStringExtra("goalId")!!

        binding.habitsRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.addHabitButton.setOnClickListener {
            val intent = Intent(this, AddHabitActivity::class.java)
            intent.putExtra("goalId", goalId)
            startActivity(intent)
        }

        lifecycleScope.launch {
            val goal = firestoreRepository.getGoals().find { it.id == goalId }
            if (goal != null) {
                binding.goalName.text = goal.name
                binding.goalScore.text = "Score: ${goal.score}"
            }

            val habits = firestoreRepository.getHabits(goalId)
            binding.habitsRecyclerView.adapter = HabitAdapter(habits) { habit ->
                habit.happyMonkeyCount++
                lifecycleScope.launch {
                    firestoreRepository.saveHabit(habit)
                    val updatedHabits = firestoreRepository.getHabits(goalId)
                    (binding.habitsRecyclerView.adapter as HabitAdapter).updateData(updatedHabits)
                    val remaining = habit.targetCount - habit.happyMonkeyCount
                    if (remaining > 0) {
                        Toast.makeText(this@GoalDetailActivity, "Alright! Great job! You're just $remaining away from nailing this habit.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@GoalDetailActivity, "Congratulations! You have nailed this habit!", Toast.LENGTH_SHORT).show()
                    }

                    if (goal != null) {
                        goal.score++
                        firestoreRepository.saveGoal(goal)
                        binding.goalScore.text = "Score: ${goal.score}"
                    }
                }
            }
        }
    }
}
