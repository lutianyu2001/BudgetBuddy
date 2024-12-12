package com.cs407.budgetbuddy.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.cs407.budgetbuddy.R
import com.cs407.budgetbuddy.databinding.ActivityMainBinding
import com.cs407.budgetbuddy.ui.auth.LoginActivity
import com.cs407.budgetbuddy.ui.common.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is logged in, if not, redirect to LoginActivity
        if (viewModel.userSession.value == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        observeViewModel()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Setup bottom navigation
        binding.bottomNavigation.setupWithNavController(navController)

        // Define top-level destinations
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_analysis,
                R.id.navigation_settings
            )
        )

        // Handle navigation visibility
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home,
                R.id.navigation_analysis,
                R.id.navigation_settings -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                }
                else -> binding.bottomNavigation.visibility = View.GONE
            }
        }
    }

    private fun observeViewModel() {
        viewModel.userSession.observe(this) { session ->
            if (session == null) {
                // User session expired, return to login
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}