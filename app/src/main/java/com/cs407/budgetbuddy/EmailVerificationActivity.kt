package com.cs407.budgetbuddy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class EmailVerificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_verify)

        val buttonResendEmail = findViewById<Button>(R.id.buttonResendEmail)
        val buttonBackToLoginView = findViewById<Button>(R.id.buttonBackToLoginView)

        buttonResendEmail.setOnClickListener {
            // TODO: Implement resend email logic
        }

        buttonBackToLoginView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
