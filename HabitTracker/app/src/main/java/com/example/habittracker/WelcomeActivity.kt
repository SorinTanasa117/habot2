package com.example.habittracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.habittracker.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.message.text = """
            Welcome to Habit Tracker!

            Did you know that it takes an average of 66 days to form a new habit?
            But don't worry, we are here to help you on your journey.

            Our app provides a simple and effective way to start living the life of your dreams,
            but you have to be truthful with yourself and us for that to happen.
            We offer no guarantee, just a chance for a change.

            Let's get started!
        """.trimIndent()

        binding.continueButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
