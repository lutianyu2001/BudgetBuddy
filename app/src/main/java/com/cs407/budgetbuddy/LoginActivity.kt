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
import androidx.lifecycle.ViewModelProvider
import com.cs407.budgetbuddy.data.BudgetDatabase
import com.cs407.budgetbuddy.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class LoginActivity(
    private val injectedUserViewModel: UserViewModel? = null
) : AppCompatActivity() {
    private var editTextUsername = findViewById<EditText>(R.id.editTextUsername)
    private var editTextPassword = findViewById<EditText>(R.id.editTextPassword)
    private var buttonLogin = findViewById<Button>(R.id.buttonLogin)
    private var buttonSignUp = findViewById<Button>(R.id.buttonToSignUpView)
    private var textViewForgotPassword = findViewById<TextView>(R.id.textViewForgotPassword)
    private var textViewContactUs = findViewById<TextView>(R.id.textViewContactUs)

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
            // TODO: Implement login logic

            if (username.isBlank() || password.isBlank()) {

            } else {
                lifecycleScope.launch {
                    val isAuthenticated = getUsernamePasswd(username, password)
                    if (isAuthenticated) {
                        val newState = UserState(userId.toInt(), username, password)

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
                userId = 0
                return false
            }
        } else {
            // user doesn't exist in SharedPreferences
            withContext(Dispatchers.IO) {
                val newUser = User(username = username)
                userId = budgetDB.userDao().insertUser(newUser)
            }

            // create a new user and store it with its hashed password in SharedPreferences
            with(userPasswdKV.edit()) {
                putString(username, passwdHashed)
                apply()
            }
        }
        return true
    }

    private fun hash(input: String): String{
        return MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            .fold("") {str, it -> str + "%02x".format(it) }
    }
}