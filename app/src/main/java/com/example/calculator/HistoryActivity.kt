package com.example.calculator

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_layout)

        val tvHistoryContent = findViewById<TextView>(R.id.tvHistoryContent)
        val history = intent.getStringArrayListExtra("history") ?: arrayListOf()
        tvHistoryContent.text = history.joinToString("\n")

        // Add click listener for Back button
        findViewById<Button>(R.id.btnBackToMain).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish() // Ensure that the HistoryActivity is finished
        }
    }
}
