package com.example.habittracker.ui.goals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.R
import com.example.habittracker.data.Goal
import com.example.habittracker.data.Habit
import com.example.habittracker.db.FirestoreRepository
import com.example.habittracker.ui.habits.HabitAdapter
import kotlinx.coroutines.launch

import androidx.lifecycle.LifecycleCoroutineScope

class GoalAdapter(
    private val goals: List<Goal>,
    private val onGoalClicked: (Goal) -> Unit,
    private val lifecycleScope: LifecycleCoroutineScope
) :
    RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    private val firestoreRepository = FirestoreRepository()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_goal, parent, false)
        return GoalViewHolder(view)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]
        holder.bind(goal)
        holder.itemView.setOnClickListener { onGoalClicked(goal) }
    }

    override fun getItemCount() = goals.size

    inner class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val goalName: TextView = itemView.findViewById(R.id.goalName)
        private val goalScore: TextView = itemView.findViewById(R.id.goalScore)
        private val happyApeIcon: ImageView = itemView.findViewById(R.id.happyApeIcon)
        private val happyApeCount: TextView = itemView.findViewById(R.id.happyApeCount)
        private val expandIcon: ImageView = itemView.findViewById(R.id.expandIcon)
        private val habitsRecyclerView: RecyclerView = itemView.findViewById(R.id.habitsRecyclerView)
        private val goalHeader: View = itemView.findViewById(R.id.goalHeader)

        fun bind(goal: Goal) {
            goalName.text = goal.name
            goalScore.text = "Score: ${goal.score}"
            val apes = goal.score / 20
            if (apes > 0) {
                happyApeIcon.visibility = View.VISIBLE
                happyApeCount.visibility = View.VISIBLE
                happyApeCount.text = "x$apes"
            } else {
                happyApeIcon.visibility = View.GONE
                happyApeCount.visibility = View.GONE
            }

            goalHeader.setOnClickListener {
                if (habitsRecyclerView.visibility == View.GONE) {
                    habitsRecyclerView.visibility = View.VISIBLE
                    expandIcon.setImageResource(R.drawable.ic_expand_less)
                    loadHabits(goal.id)
                } else {
                    habitsRecyclerView.visibility = View.GONE
                    expandIcon.setImageResource(R.drawable.ic_expand_more)
                }
            }
        }

        private fun loadHabits(goalId: String) {
            lifecycleScope.launch {
                val habits = firestoreRepository.getHabits(goalId)
                habitsRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
                habitsRecyclerView.adapter = HabitAdapter(habits) { habit ->
                    // Handle habit click
                }
            }
        }
    }
}
