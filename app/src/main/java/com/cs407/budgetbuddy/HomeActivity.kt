package com.cs407.budgetbuddy

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cs407.budgetbuddy.data.BudgetDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    // the parameters for user information (like username, password, email, phoneNumber, userId) are stored here
    private lateinit var userViewModel: UserViewModel
    private lateinit var budgetDB: BudgetDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomNaviationView: BottomNavigationView = findViewById(R.id.bottomNavigationHome)
        bottomNaviationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_transactions -> {
                    setContentView(R.layout.activity_transaction_details)
                    true
                }
                R.id.navigation_analysis -> {
                    setContentView(R.layout.activity_analysis)
                    true
                }
                R.id.navigation_settings -> {
                    true
                }
                else -> false
            }
        }

        budgetDB = BudgetDatabase.getDatabase(this)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        lifecycleScope.launch {
            try {
                setUserViewModel()
            } catch (e: Exception) {
                Log.println(Log.VERBOSE, "HomeActivity", "Failed to load user information")
            }
        }
        showToast("Welcome ${userViewModel.userState.value.username}")
    }

    private suspend fun setUserViewModel() {
        withContext(Dispatchers.IO) {
            val username = intent.getStringExtra("username") ?: run {
                return@withContext
            }
            Log.println(Log.VERBOSE, "HomeActivity", "username $username")

            val password = intent.getStringExtra("password") ?: run {
                return@withContext
            }
            Log.println(Log.VERBOSE, "HomeActivity", "password $password")

            val email = budgetDB.userDao().getUserEmail(username) ?: "No email found"
            Log.println(Log.VERBOSE, "HomeActivity", "email $email")

            val phoneNumber = budgetDB.userDao().getUserPhoneNumber(username) ?: "No username found"
            Log.println(Log.VERBOSE, "HomeActivity", "phoneNumber $phoneNumber")

            val userId = budgetDB.userDao().getUserId(username)
            Log.println(Log.VERBOSE, "HomeActivity", "userId $userId") ?: "No id found"

            userViewModel.setUser(
                UserState(
                    id = userId,
                    username = username,
                    password = password,
                    email = email,
                    phoneNumber = phoneNumber
                )
            )
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}