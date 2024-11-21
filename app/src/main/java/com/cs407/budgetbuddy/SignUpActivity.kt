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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity(
    private val injectedUserViewModel: UserViewModel? = null
) : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var userPasswdKV: SharedPreferences
    private lateinit var budgetDB: BudgetDatabase
    private lateinit var auth: FirebaseAuth

    private var userId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = Firebase.auth
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
            if (phoneNumber != null && phoneNumber.matches(Regex("^\\d{11}$"))) {
                Log.println(Log.VERBOSE, "SignUpActivity", "Phone number has incorrect format")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val isUsernameTaken = withContext(Dispatchers.IO) {
                    checkUsernameTaken(username)
                }
                if (isUsernameTaken) {
                    Log.println(Log.VERBOSE, "SignUpActivity", "Username is taken")
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                                    if (verificationTask.isSuccessful) {
                                        Log.println(Log.VERBOSE, "SignUpActivity", "Verification email sent")
                                        saveUserToDatabaseAndSharedPrefs(username, email, phoneNumber, password)
                                        startActivity(Intent(this@SignUpActivity, EmailVerificationActivity::class.java))
                                    } else {
                                        Log.println(Log.ERROR, "SignUpActivity", "Verification email failed to send")
                                    }
                                }
                            } else {
                                Log.println(Log.ERROR, "SignUpActivity", "The user didn't get created")
                            }
                        }
                }
            }
        }

        buttonToLoginView.setOnClickListener {
            finish() //take the user back to the LoginActivity
        }
    }

    private fun saveUserToDatabaseAndSharedPrefs(username: String, email: String, phoneNumber: String, password: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val newUser = User(username = username, email = email, phoneNumber = phoneNumber)
            budgetDB.userDao().insertUser(newUser)

            val hashedPasswd = hash(password)
            with (userPasswdKV.edit()) {
                putString(username, hashedPasswd)
                apply()
            }
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
