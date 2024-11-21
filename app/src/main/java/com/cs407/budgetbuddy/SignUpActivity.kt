package com.cs407.budgetbuddy

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cs407.budgetbuddy.data.BudgetDatabase
import com.cs407.budgetbuddy.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest

class SignUpActivity(
    private val injectedUserViewModel: UserViewModel? = null
) : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var userPasswdKV: SharedPreferences
    private lateinit var budgetDB: BudgetDatabase

    private var userId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        userPasswdKV = getSharedPreferences(getString(R.string.userPasswdKV), Context.MODE_PRIVATE)
        budgetDB = BudgetDatabase.getDatabase(this)

        val editTextUsername = findViewById<EditText>(R.id.editTextSignUpUsername)
        val editTextEmail = findViewById<EditText>(R.id.editTextSignUpEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextSignUpPassword)
        val editTextConfirmPassword = findViewById<EditText>(R.id.editTextSignUpConfirmPassword)
        val editTextPhoneNumber = findViewById<EditText>(R.id.phoneInput)
        val buttonSignUp = findViewById<Button>(R.id.buttonSignUp)
        val buttonToLoginView = findViewById<Button>(R.id.buttonToLoginView)

        buttonSignUp.setOnClickListener {
            val username = editTextUsername.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val confirmPassword = editTextConfirmPassword.text.toString()
            val phoneNumber = editTextPhoneNumber.text.toString()


            // TODO adjust UI so that error TextViews will report if there has been blank input
            if (username.isBlank()) {
                Log.println(Log.VERBOSE, "SignUpActivity", "Username cannot be blank")
                return@setOnClickListener
            } else if (email.isBlank()) {
                Log.println(Log.VERBOSE, "SignUpActitivy", "Email cannot be blank")
                return@setOnClickListener
            } else if (password.isBlank()) {
                Log.println(Log.VERBOSE, "SignUpActitivy", "Password cannot be blank")
                return@setOnClickListener
            } else if (confirmPassword.isBlank()) {
                Log.println(Log.VERBOSE, "SignUpActitivy", "Password confirmation cannot be blank")
                return@setOnClickListener
            }

            // TODO adjust UI so that an error TextView will report if there is a difference between the first and second password entry
            if (password != confirmPassword) {
                Log.println(Log.VERBOSE, "SignUpActivity", "Entered passwords do not match.")
                return@setOnClickListener
            }

            // TODO adjust UI so that error TextView will report if invalid phone number is entered
            if (phoneNumber != null && phoneNumber.matches(Regex("^\\d{10}$"))) {
                Log.println(Log.VERBOSE, "SignUpActivity", "Phone number has incorrect format")
                return@setOnClickListener
            }

            // check if given username/password combination is available
            lifecycleScope.launch {
                val isUsernameTaken = withContext(Dispatchers.IO) {
                    checkUsernameTaken(username)
                }
                if (isUsernameTaken) {
                    // TODO adjust UI so that an error TextView will report that a username is taken
                    Log.println(Log.VERBOSE, "SignUpActivity", "Username is taken")
                    return@launch
                } else {
                    // add the new user to the budgetDB
                    withContext(Dispatchers.IO) {
                        val newUser = User(username = username, email = email, phoneNumber = phoneNumber)
                        userId = budgetDB.userDao().insertUser(newUser)
                    }

                    // store the hashed password in SharedPreferences
                    with (userPasswdKV.edit()) {
                        val passwdHashed = hash(password)
                        putString(username, passwdHashed)
                        apply()
                    }
                }
            }

            startActivity(Intent(this, EmailVerificationActivity::class.java))
        }

        buttonToLoginView.setOnClickListener {
            finish() //take the user back to the LoginActivity
        }
    }

    private suspend fun checkUsernameTaken(
        username: String
    ): Boolean {
        val userExists = userPasswdKV.contains(username)
        return userExists
    }

    private fun hash(input: String): String{
        return MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            .fold("") {str, it -> str + "%02x".format(it) }
    }
}
