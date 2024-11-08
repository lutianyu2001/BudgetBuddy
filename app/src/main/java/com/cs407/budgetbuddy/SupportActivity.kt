package com.cs407.budgetbuddy

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SupportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)

        val buttonBackToLogin = findViewById<Button>(R.id.buttonBackToLogin)
        buttonBackToLogin.setOnClickListener {
            finish() // This will take user back to login screen
        }
    }
} 