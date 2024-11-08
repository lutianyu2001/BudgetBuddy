package com.cs407.budgetbuddy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val editTextEmail = findViewById<EditText>(R.id.editTextResetEmail)
        val buttonVerify = findViewById<Button>(R.id.buttonVerify)
        val buttonBack = findViewById<Button>(R.id.buttonBackToLogin)

        buttonVerify.setOnClickListener {
            val email = editTextEmail.text.toString()
            // TODO: Add email validation logic here
            startActivity(Intent(this, EmailVerificationActivity::class.java))
        }

        buttonBack.setOnClickListener {
            finish() // This will take user back to login screen
        }
    }
} 