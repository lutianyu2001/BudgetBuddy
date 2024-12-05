package com.cs407.budgetbuddy

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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

        userViewModel = injectedUserViewModel ?: UserViewModel()

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
                showToast("Username cannot be blank.")
                Log.println(Log.VERBOSE, "SignUpActivity", "Username cannot be blank")
                return@setOnClickListener
            } else if (email.isBlank()) {
                showToast("Email cannot be blank.")
                Log.println(Log.VERBOSE, "SignUpActitivy", "Email cannot be blank")
                return@setOnClickListener
            } else if (password.isBlank()) {
                showToast("Password cannot be blank.")
                Log.println(Log.VERBOSE, "SignUpActitivy", "Password cannot be blank")
                return@setOnClickListener
            } else if (confirmPassword.isBlank()) {
                showToast("Password confirmation cannot be blank.")
                Log.println(Log.VERBOSE, "SignUpActitivy", "Password confirmation cannot be blank")
                return@setOnClickListener
            }

            // TODO adjust UI so that an error TextView will report if there is a difference between the first and second password entry
            if (password != confirmPassword) {
                showToast("Entered passwords do not match.")
                Log.println(Log.VERBOSE, "SignUpActivity", "Entered passwords do not match.")
                return@setOnClickListener
            }

            // TODO adjust UI so that error TextView will report if invalid phone number is entered
            if (phoneNumber != null && phoneNumber.matches(Regex("^\\d{11}$"))) {
                showToast("Phone number has incorrect format.")
                Log.println(Log.VERBOSE, "SignUpActivity", "Phone number has incorrect format")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val isUsernameTaken = withContext(Dispatchers.IO) {
                    checkUsernameTaken(username)
                }
                if (isUsernameTaken) {
                    showToast("Username is taken.")
                    Log.println(Log.VERBOSE, "SignUpActivity", "Username is taken")
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.println(Log.VERBOSE, "SignUpActivity", "User created successfully, navigating to email verification activity")
                                userViewModel.setUser(UserState(id = 0, username = username, email = email, password = password))
                                Log.println(Log.VERBOSE, "SignUpActivity", "username ${username} password ${password} email ${email}")

                                val intent = Intent(this@SignUpActivity, EmailVerificationActivity::class.java).apply {
                                    putExtra("username", username)
                                    putExtra("password", password)
                                    putExtra("email", email)
                                    putExtra("phoneNumber", phoneNumber)
                                }

                                startActivity(intent)
                            } else {
                                Log.println(
                                    Log.ERROR,
                                    "SignUpActivity",
                                    "User verification failed"
                                )
                            }
                        }
                }
            }
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


    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
