package com.cs407.budgetbuddy

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

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

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        val username = userViewModel._userState.value.username
        if (!username.isNullOrBlank()) {
            Toast.makeText(this, "Welcome $username", Toast.LENGTH_LONG).show()
        }
    }
}