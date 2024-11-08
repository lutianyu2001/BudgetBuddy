package com.cs407.budgetbuddy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val editTextUsername = findViewById<EditText>(R.id.editTextSignUpUsername)
        val editTextEmail = findViewById<EditText>(R.id.editTextSignUpEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextSignUpPassword)
        val editTextConfirmPassword = findViewById<EditText>(R.id.editTextSignUpConfirmPassword)
        val buttonSignUp = findViewById<Button>(R.id.buttonSignUp)
        val buttonToLoginView = findViewById<Button>(R.id.buttonToLoginView)

        buttonSignUp.setOnClickListener {
            val username = editTextUsername.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val confirmPassword = editTextConfirmPassword.text.toString()
            // TODO: Implement sign up logic

            startActivity(Intent(this, EmailVerificationActivity::class.java))
        }

        buttonToLoginView.setOnClickListener {
            finish() //take the user back to the LoginActivity
        }
    }
}
