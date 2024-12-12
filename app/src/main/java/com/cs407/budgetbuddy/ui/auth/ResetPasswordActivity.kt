package com.cs407.budgetbuddy.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cs407.budgetbuddy.databinding.ActivityResetPasswordBinding
import com.google.android.material.snackbar.Snackbar

/**
 * Activity for handling password reset requests
 */
class ResetPasswordActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityResetPasswordBinding
    private val viewModel: ResetPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.apply {
            buttonVerify.setOnClickListener {
                val email = editTextResetEmail.text.toString()
                viewModel.requestPasswordReset(email)
            }

            buttonBackToLogin.setOnClickListener {
                finish()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.resetState.observe(this) { state ->
            when (state) {
                is ResetPasswordState.Loading -> {
                    setLoading(true)
                }
                is ResetPasswordState.VerificationSent -> {
                    setLoading(false)
                    navigateToVerification(state.email)
                }
                is ResetPasswordState.Error -> {
                    setLoading(false)
                    showError(state.message)
                }
                ResetPasswordState.Initial -> {
                    setLoading(false)
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.apply {
            buttonVerify.isEnabled = !isLoading
            progressBar.visibility = if (isLoading) {
                android.view.View.VISIBLE
            } else {
                android.view.View.GONE
            }
        }
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(android.R.color.holo_red_light))
            .show()
    }

    private fun navigateToVerification(email: String) {
        val intent = Intent(this, EmailVerificationActivity::class.java).apply {
            putExtra(EmailVerificationActivity.EXTRA_EMAIL, email)
        }
        startActivity(intent)
        finish()
    }
}