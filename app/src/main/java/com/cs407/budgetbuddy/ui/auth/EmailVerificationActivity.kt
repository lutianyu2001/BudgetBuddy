package com.cs407.budgetbuddy.ui.auth

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cs407.budgetbuddy.databinding.ActivityEmailVerificationBinding
import com.google.android.material.snackbar.Snackbar

/**
 * Activity for handling email verification process
 */
class EmailVerificationActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityEmailVerificationBinding
    private val viewModel: EmailVerificationViewModel by viewModels()
    private var resendTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringExtra(EXTRA_EMAIL)?.let { email ->
            viewModel.setEmail(email)
        } ?: run {
            finish()
            return
        }

        setupViews()
        observeViewModel()
        startResendTimer()
    }

    private fun setupViews() {
        binding.apply {
            buttonVerify.setOnClickListener {
                val code = editTextVerificationCode.text.toString()
                viewModel.verifyCode(code)
            }

            buttonResendEmail.setOnClickListener {
                viewModel.resendVerificationCode()
                startResendTimer()
            }

            buttonBack.setOnClickListener {
                finish()
            }

            // Set up verification code input
            editTextVerificationCode.apply {
                filters = arrayOf(android.text.InputFilter.LengthFilter(6))
                setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                        viewModel.verifyCode(text.toString())
                        true
                    } else {
                        false
                    }
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.verificationState.observe(this) { state ->
            when (state) {
                is VerificationState.Loading -> {
                    setLoading(true)
                }
                is VerificationState.Success -> {
                    setLoading(false)
                    showSuccess("Email verified successfully")
                    finish()
                }
                is VerificationState.Error -> {
                    setLoading(false)
                    showError(state.message)
                }
                is VerificationState.Initial -> {
                    setLoading(false)
                }
            }
        }

        viewModel.email.observe(this) { email ->
            binding.textViewEmailSent.text = getString(
                com.cs407.budgetbuddy.R.string.verification_email_sent,
                email
            )
        }

        viewModel.resendEnabled.observe(this) { enabled ->
            binding.buttonResendEmail.isEnabled = enabled
        }
    }

    private fun startResendTimer() {
        resendTimer?.cancel()
        
        resendTimer = object : CountDownTimer(RESEND_DELAY_MS, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                binding.buttonResendEmail.text = getString(
                    com.cs407.budgetbuddy.R.string.resend_code_timer,
                    seconds
                )
            }

            override fun onFinish() {
                binding.buttonResendEmail.text = getString(
                    com.cs407.budgetbuddy.R.string.resend_code
                )
                viewModel.enableResend()
            }
        }.start()
    }

    private fun setLoading(isLoading: Boolean) {
        binding.apply {
            buttonVerify.isEnabled = !isLoading
            buttonResendEmail.isEnabled = !isLoading && viewModel.resendEnabled.value == true
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

    override fun onDestroy() {
        super.onDestroy()
        resendTimer?.cancel()
    }

    companion object {
        const val EXTRA_EMAIL = "extra_email"
        private const val RESEND_DELAY_MS = 60_000L // 60 seconds
    }
}