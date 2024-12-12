package com.cs407.budgetbuddy.ui.profile

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cs407.budgetbuddy.databinding.ActivityEditProfileBinding
import com.cs407.budgetbuddy.model.UserProfile
import com.cs407.budgetbuddy.ui.common.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: EditProfileViewModel by viewModels {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.apply {
            toolbar.setNavigationOnClickListener { finish() }

            buttonSave.setOnClickListener {
                val username = editTextUsername.text.toString()
                val email = editTextEmail.text.toString()
                viewModel.updateProfile(username, email)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.userProfile.observe(this) { profile ->
            updateUI(profile)
        }

        viewModel.events.observe(this) { event ->
            when (event) {
                is EditProfileEvent.ShowError -> {
                    showError(event.message)
                }
                is EditProfileEvent.ShowSuccess -> {
                    showSuccess(event.message)
                    finish()
                }
            }
        }
    }

    private fun updateUI(profile: UserProfile) {
        binding.apply {
            editTextUsername.setText(profile.username)
            editTextEmail.setText(profile.email ?: "")
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