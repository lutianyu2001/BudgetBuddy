package com.cs407.budgetbuddy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.security.MessageDigest
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.cs407.budgetbuddy.data.BudgetDatabase
import com.cs407.budgetbuddy.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class LoginActivity(
    private val injectedUserViewModel: UserViewModel? = null
) : AppCompatActivity() {
    private lateinit var editTextUsername:EditText
    private lateinit var editTextPassword:EditText
    private lateinit var buttonLogin:Button
    private lateinit var buttonSignUp:Button
    private lateinit var textViewForgotPassword:TextView
    private lateinit var textViewContactUs:TextView

    private lateinit var userViewModel: UserViewModel

    private lateinit var userPasswdKV: SharedPreferences
    private lateinit var budgetDB: BudgetDatabase

    private var userId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editTextUsername = findViewById<EditText>(R.id.editTextUsername)
        editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        buttonLogin = findViewById<Button>(R.id.buttonLogin)
        buttonSignUp = findViewById<Button>(R.id.buttonToSignUpView)
        textViewForgotPassword = findViewById<TextView>(R.id.textViewForgotPassword)
        textViewContactUs = findViewById<TextView>(R.id.textViewContactUs)

        // TODO initialize budget database
        budgetDB = BudgetDatabase.getDatabase(this)

        userViewModel = if (injectedUserViewModel != null) {
            injectedUserViewModel
        } else {
            ViewModelProvider(this).get(UserViewModel::class.java)
        }

        userPasswdKV = getSharedPreferences(getString(R.string.userPasswdKV), Context.MODE_PRIVATE)

        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()

            if (username.isBlank() ||
                password.isBlank()) {
                showToast("Username or password is blank.")
                Log.println(Log.VERBOSE, "LoginFragment", "username or password is blank")
            } else {
                lifecycleScope.launch {
                    // validates whether or not the username is in sharedPreferences, and also if the password entered is correct
                    val isAuthenticated = withContext(Dispatchers.IO) {
                        getUsernamePasswd(username, password)
                    }
                    if (isAuthenticated) {
                        val email = budgetDB.userDao().getUserEmail(username)
                        val newState = UserState(id = userId.toInt(),
                            username = username,
                            email = email,
                            password = password)
                        userViewModel.setUser(newState)

                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        intent.putExtra("username", username)
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    } else {
                        showToast("Username and password combination is incorrect.")
                        Log.println(Log.VERBOSE, "LoginFragment", "Username incorrect or does not match")
                    }
                }
            }
        }

        buttonSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        textViewForgotPassword.setOnClickListener {
            startActivity(Intent(this, ResetPasswordActivity::class.java))
        }

        textViewContactUs.setOnClickListener {
            startActivity(Intent(this, SupportActivity::class.java))
        }
    }


    private suspend fun getUsernamePasswd(
        username: String,
        passwdPlain: String
    ): Boolean {
        val passwdHashed = hash(passwdPlain)

        val userExists = userPasswdKV.contains(username)

        if (userExists) {
            val passwdStored = userPasswdKV.getString(username, null)
            userId = budgetDB.userDao().getUserId(username)

            // entered password does not match
            if (passwdStored != passwdHashed) {
                showToast("The entered password does not match.")
                Log.println(Log.VERBOSE, "LoginActivity", "Entered password does not match")
                userId = 0
                return false
            }
        } else {
            // user doesn't exist in the database
            // so add an error text view to report this to the user
            showToast("The entered username and password combination does not exist.")
            Log.println(Log.VERBOSE, "LoginActivity", "Entered username/password does not exist")
            return false
        }
        return true
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