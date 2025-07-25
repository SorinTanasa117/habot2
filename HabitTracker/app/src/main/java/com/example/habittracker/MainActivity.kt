package com.example.habittracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.habittracker.data.Goal
import com.example.habittracker.databinding.ActivityMainBinding
import com.example.habittracker.db.FirestoreRepository
import com.example.habittracker.ui.goals.GoalAdapter
import com.example.habittracker.ui.goals.GoalDetailActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val firestoreRepository = FirestoreRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.goalsRecyclerView.layoutManager = LinearLayoutManager(this)

        binding.addGoalButton.setOnClickListener {
            startActivity(Intent(this, AddGoalActivity::class.java))
        }

        binding.profileButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        lifecycleScope.launch {
            val goals = firestoreRepository.getGoals()
            binding.goalsRecyclerView.adapter = GoalAdapter(goals, { goal ->
                val intent = Intent(this@MainActivity, GoalDetailActivity::class.java)
                intent.putExtra("goalId", goal.id)
                startActivity(intent)
            }, lifecycleScope)
        }
    }
}
