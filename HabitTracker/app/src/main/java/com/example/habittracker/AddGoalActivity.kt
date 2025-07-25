package com.example.habittracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.example.habittracker.data.Goal
import com.example.habittracker.databinding.ActivityAddGoalBinding
import com.example.habittracker.db.FirestoreRepository
import kotlinx.coroutines.launch
import java.util.UUID

class AddGoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddGoalBinding
    private val firestoreRepository = FirestoreRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val confidenceLevels = arrayOf("Beginner", "Intermediate", "Advanced")
        val targetLevels = arrayOf("Advanced", "Expert")
        val feelings = arrayOf("Afraid", "Apprehensive", "Neutral", "Hopeful", "Excited")

        binding.currentConfidence.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, confidenceLevels)
        binding.targetLevel.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, targetLevels)
        binding.feelings.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, feelings)

        binding.saveGoalButton.setOnClickListener {
            val goal = Goal(
                id = UUID.randomUUID().toString(),
                name = binding.goalName.text.toString(),
                currentConfidence = binding.currentConfidence.selectedItem.toString(),
                targetLevel = binding.targetLevel.selectedItem.toString(),
                feelings = binding.feelings.selectedItem.toString()
            )
            lifecycleScope.launch {
                firestoreRepository.saveGoal(goal)
                finish()
            }
        }
    }
}
