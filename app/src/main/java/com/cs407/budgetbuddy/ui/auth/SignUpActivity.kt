package com.cs407.budgetbuddy.ui.auth

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cs407.budgetbuddy.databinding.ActivitySignUpBinding
import com.cs407.budgetbuddy.model.SignUpForm
import com.cs407.budgetbuddy.model.SignUpState
import com.cs407.budgetbuddy.ui.common.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    private val viewModel: SignUpViewModel by viewModels {
        ViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.apply {
            buttonSignUp.setOnClickListener {
                val form = SignUpForm(
                    username = editTextSignUpUsername.text.toString(),
                    email = editTextSignUpEmail.text.toString(),
                    password = editTextSignUpPassword.text.toString(),
                    confirmPassword = editTextSignUpConfirmPassword.text.toString()
                )
                viewModel.signUp(form)
            }

            buttonToLoginView.setOnClickListener {
                finish()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.signUpState.observe(this) { state ->
            when (state) {
                is SignUpState.Loading -> {
                    setLoading(true)
                }
                is SignUpState.Success -> {
                    setLoading(false)
                    showSuccess("Account created successfully")
                    finish()
                }
                is SignUpState.Error -> {
                    setLoading(false)
                    showError(state.message)
                }
                SignUpState.Initial -> {
                    setLoading(false)
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.apply {
            buttonSignUp.isEnabled = !isLoading
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

    private fun showSuccess(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(getColor(android.R.color.holo_green_light))
            .show()
    }
}