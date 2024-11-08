package com.cs407.badgerbudget

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cs407.budgetbuddy1.R

class EmailVerificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_verify)

        val buttonResendEmail = findViewById<Button>(R.id.buttonResendEmail)
        val textViewBackToLogin = findViewById<TextView>(R.id.textViewBackToLogin)

        buttonResendEmail.setOnClickListener {
            // TODO: Implement resend email logic
        }

        textViewBackToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}