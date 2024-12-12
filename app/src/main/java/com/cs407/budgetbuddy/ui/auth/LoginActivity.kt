package com.cs407.budgetbuddy.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cs407.budgetbuddy.databinding.ActivityLoginBinding
import com.cs407.budgetbuddy.model.LoginState
import com.cs407.budgetbuddy.ui.main.MainActivity
import com.cs407.budgetbuddy.ui.support.SupportActivity
import com.google.android.material.snackbar.Snackbar
import com.cs407.budgetbuddy.ui.common.ViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    // Updated ViewModel initialization using ViewModelFactory
    private val viewModel: LoginViewModel by viewModels {
        ViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.apply {
            buttonLogin.setOnClickListener {
                val username = editTextUsername.text.toString()
                val password = editTextPassword.text.toString()
                viewModel.login(username, password)
            }

            buttonSignUp.setOnClickListener {
                startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
            }

            textViewForgotPassword.setOnClickListener {
                startActivity(Intent(this@LoginActivity, ResetPasswordActivity::class.java))
            }

            textViewContactUs.setOnClickListener {
                startActivity(Intent(this@LoginActivity, SupportActivity::class.java))
            }

            switchRememberMe.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setRememberMe(isChecked)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginState.Loading -> {
                    setLoading(true)
                }
                is LoginState.Success -> {
                    setLoading(false)
                    navigateToMain()
                }
                is LoginState.Error -> {
                    setLoading(false)
                    showError(state.message)
                }
                LoginState.Initial -> {
                    setLoading(false)
                }
            }
        }

        viewModel.savedCredentials.observe(this) { credentials ->
            binding.apply {
                editTextUsername.setText(credentials.username)
                editTextPassword.setText(credentials.password)
                switchRememberMe.isChecked = credentials.rememberMe
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.apply {
            buttonLogin.isEnabled = !isLoading
            progressBar.visibility = if (isLoading) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }
        }
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}