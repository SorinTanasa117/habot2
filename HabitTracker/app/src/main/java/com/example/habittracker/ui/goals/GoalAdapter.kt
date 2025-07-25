package com.example.habittracker.ui.goals

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.R
import com.example.habittracker.data.Goal

class GoalAdapter(private val goals: List<Goal>, private val onGoalClicked: (Goal) -> Unit) :
    RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

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

    class GoalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val goalName: TextView = itemView.findViewById(R.id.goalName)
        private val goalScore: TextView = itemView.findViewById(R.id.goalScore)
        private val happyApeIcon: ImageView = itemView.findViewById(R.id.happyApeIcon)
        private val happyApeCount: TextView = itemView.findViewById(R.id.happyApeCount)

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
        }
    }
}
