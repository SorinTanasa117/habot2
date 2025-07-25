package com.example.habittracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.habittracker.data.Habit
import com.example.habittracker.databinding.ActivityAddHabitBinding
import com.example.habittracker.db.FirestoreRepository
import kotlinx.coroutines.launch
import java.util.UUID

class AddHabitActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddHabitBinding
    private val firestoreRepository = FirestoreRepository()
    private lateinit var goalId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHabitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        goalId = intent.getStringExtra("goalId")!!

        val confidenceLevels = arrayOf("Beginner", "Intermediate", "Advanced")
        val targetLevels = arrayOf("Advanced", "Expert")
        val feelings = arrayOf("Afraid", "Apprehensive", "Neutral", "Hopeful", "Excited")
        val commonHabits = resources.getStringArray(R.array.common_habits)

        binding.currentConfidence.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, confidenceLevels)
        binding.targetLevel.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, targetLevels)
        binding.feelings.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, feelings)
        binding.habitName.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, commonHabits))

        binding.habitName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (commonHabits.contains(s.toString())) {
                    binding.addCustomHabitButton.visibility = GONE
                } else {
                    binding.addCustomHabitButton.visibility = VISIBLE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.addCustomHabitButton.setOnClickListener {
            Toast.makeText(this, "Custom habit added!", Toast.LENGTH_SHORT).show()
        }

        binding.saveHabitButton.setOnClickListener {
            val habit = Habit(
                id = UUID.randomUUID().toString(),
                goalId = goalId,
                name = binding.habitName.text.toString(),
                description = binding.description.text.toString(),
                currentConfidence = binding.currentConfidence.selectedItem.toString(),
                targetLevel = binding.targetLevel.selectedItem.toString(),
                feelings = binding.feelings.selectedItem.toString(),
                targetCount = binding.targetCount.text.toString().toInt()
            )
            lifecycleScope.launch {
                firestoreRepository.saveHabit(habit)
                val goal = firestoreRepository.getGoal(goalId)
                if (goal != null) {
                    goal.score++
                    firestoreRepository.saveGoal(goal)
                }
                finish()
            }
        }
    }
}
