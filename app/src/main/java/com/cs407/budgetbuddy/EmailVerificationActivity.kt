package com.cs407.budgetbuddy

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cs407.budgetbuddy.data.BudgetDatabase
import com.cs407.budgetbuddy.data.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class EmailVerificationActivity(): AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var userPasswdKV: SharedPreferences
    private lateinit var budgetDB: BudgetDatabase
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_verify)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userViewModel.setUser(
            UserState(
            id = 0,
            email = intent.getStringExtra("email") ?: "",
            username = intent.getStringExtra("username") ?: "",
            password = intent.getStringExtra("password") ?: "",
            phoneNumber = intent.getStringExtra("phoneNumber") ?: ""
            )
        )

        userPasswdKV = getSharedPreferences(getString(R.string.userPasswdKV), MODE_PRIVATE)
        budgetDB = BudgetDatabase.getDatabase(this)
        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser
        user?.let {
            if (!it.isEmailVerified) {
                if (!it.isEmailVerified) {
                    it.sendEmailVerification()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                Log.v("EmailVerificationActivity", "Verification email sent to ${it.email}")
                            } else {
                                Log.e("EmailVerificationActivity", "Failed to send verification email", task.exception)
                            }
                        }
                }
            }
        }

        val buttonResendEmail = findViewById<Button>(R.id.buttonResendEmail)
        val buttonBackToLoginView = findViewById<Button>(R.id.buttonBackToLoginView)

        checkIfUserVerifiedAlreadyAndSave0()

        buttonResendEmail.setOnClickListener {
            resendVerificationEmail()
            checkIfUserVerifiedAlreadyAndSave0()
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

    private fun checkIfUserVerifiedAlreadyAndSave0() {
        lifecycleScope.launch {
            checkIfUserVerifiedAlreadyAndSave()
        }
    }

    private suspend fun checkIfUserVerifiedAlreadyAndSave() {
        val user = auth.currentUser

        if (user == null) {
            Log.println(Log.ERROR, "EmailVerificationActivity", "No user logged in right now")
            return
        }

        var attempts = 0
        var maxAttempts = 10
        val pollDelayMillis = 2000L


        while (attempts < maxAttempts) {
            user.reload()
            if (user.isEmailVerified) {
                Log.println(Log.VERBOSE, "EmailVerificationActivity", "Email Verification was verified")
                saveUserToDatabaseAndSharedPrefsIfVerified()
                Log.println(Log.VERBOSE, "EmailVerificationAcivitiy", "User saved to database and shared prefs")
                return
            }
            Log.v("EmailVerificationActivity", "Email not verified yet, keep trying")
            attempts++
            delay(pollDelayMillis)
        }
        saveUserToDatabaseAndSharedPrefsIfVerified()
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
                Log.println(Log.VERBOSE,"EmailVerificationActivity", "username ${userState.username} password ${userState.password}")
                with(userPasswdKV.edit()) {
                    putString(userState.username, hash(userState.password))
                    apply()
                }
            }
        }
    }

    private fun hash(input: String): String{
        return MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            .fold("") {str, it -> str + "%02x".format(it) }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
