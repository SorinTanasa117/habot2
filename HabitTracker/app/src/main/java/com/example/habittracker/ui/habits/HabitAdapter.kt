package com.example.habittracker.ui.habits

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.habittracker.R
import com.example.habittracker.data.Habit

class HabitAdapter(
    private val habits: List<Habit>,
    private val onIidItClicked: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        holder.bind(habit)
        holder.iDidItButton.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Are you sure you did it?")
                .setMessage("Remember, sticking to your resolve to do the thing you set up is what's important, not tickling a monkey button once a day. Believe in yourself!")
                .setPositiveButton("I did it!") { _, _ ->
                    onIidItClicked(habit)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun getItemCount() = habits.size

    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val habitName: TextView = itemView.findViewById(R.id.habitName)
        private val happyMonkeyCount: TextView = itemView.findViewById(R.id.happyMonkeyCount)
        val iDidItButton: Button = itemView.findViewById(R.id.iDidItButton)

        fun bind(habit: Habit) {
            habitName.text = habit.name
            happyMonkeyCount.text = "${habit.happyMonkeyCount} \uD83D\uDC35"
        }
    }
}
