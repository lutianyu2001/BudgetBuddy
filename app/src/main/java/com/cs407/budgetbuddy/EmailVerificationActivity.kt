package com.cs407.budgetbuddy

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cs407.budgetbuddy.data.BudgetDatabase
import com.cs407.budgetbuddy.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EmailVerificationActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var userPasswdKV: SharedPreferences
    private lateinit var budgetDB: BudgetDatabase
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_verify)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userPasswdKV = getSharedPreferences(getString(R.string.userPasswdKV), MODE_PRIVATE)
        budgetDB = BudgetDatabase.getDatabase(this)
        auth = FirebaseAuth.getInstance()

        val buttonResendEmail = findViewById<Button>(R.id.buttonResendEmail)
        val buttonBackToLoginView = findViewById<Button>(R.id.buttonBackToLoginView)

        buttonResendEmail.setOnClickListener {
            resendVerificationEmail()
        }

        buttonBackToLoginView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun resendVerificationEmail() {
        val user = auth.currentUser
        if (user == null) {
            return
        }

        user.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.println(Log.VERBOSE, "EmailVerificationActivity", "Verification email resent to ${user.email}")
                    lifecycleScope.launch {
                        saveUserToDatabaseAndSharedPrefsIfVerified()
                    }
                } else {
                    Log.println(Log.ERROR, "EmailVerificationActivity", "Fail to send verification email.")
                }
            }
    }

    private suspend fun saveUserToDatabaseAndSharedPrefsIfVerified() {
        val user = auth.currentUser

        if (user == null) {
            Log.println(Log.ERROR, "EmailVerificationActivity", "No user is logged in right now")
            return
        }

        user.reload()

        if (user.isEmailVerified) {
            val userState = userViewModel.userState.value

            withContext(Dispatchers.IO) {
                val newUser = User(
                    username = userState.username,
                    email = userState.email,
                    phoneNumber = null // update if phone number is required
                )
                budgetDB.userDao().insertUser(newUser)

                // save user to shared preferences
                with(userPasswdKV.edit()) {
                    putString(userState.username, userState.password)
                    apply()
                }
            }


        }
    }
}
