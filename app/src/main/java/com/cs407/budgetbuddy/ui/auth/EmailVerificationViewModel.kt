package com.cs407.budgetbuddy.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.budgetbuddy.data.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for handling email verification logic
 */
class EmailVerificationViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _verificationState = MutableLiveData<VerificationState>(VerificationState.Initial)
    val verificationState: LiveData<VerificationState> = _verificationState

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _resendEnabled = MutableLiveData(false)
    val resendEnabled: LiveData<Boolean> = _resendEnabled

    fun setEmail(email: String) {
        _email.value = email
    }

    fun verifyCode(code: String) {
        if (!isValidCode(code)) {
            _verificationState.value = VerificationState.Error("Please enter a valid verification code")
            return
        }

        viewModelScope.launch {
            try {
                _verificationState.value = VerificationState.Loading
//                val success = userRepository.verifyEmail(_email.value!!, code)
                val success = true
                if (success) {
                    _verificationState.value = VerificationState.Success
                } else {
                    _verificationState.value = VerificationState.Error("Invalid verification code")
                }
            } catch (e: Exception) {
                _verificationState.value = VerificationState.Error(
                    e.message ?: "Failed to verify email"
                )
            }
        }
    }

    fun resendVerificationCode() {
        viewModelScope.launch {
            try {
                _verificationState.value = VerificationState.Loading
                _resendEnabled.value = false
//                val success = userRepository.resendVerificationCode(_email.value!!)
                val success = true
                if (success) {
                    _verificationState.value = VerificationState.Initial
                } else {
                    _verificationState.value = VerificationState.Error("Failed to resend code")
                    _resendEnabled.value = true
                }
            } catch (e: Exception) {
                _verificationState.value = VerificationState.Error(
                    e.message ?: "Failed to resend code"
                )
                _resendEnabled.value = true
            }
        }
    }

    fun enableResend() {
        _resendEnabled.value = true
    }

    private fun isValidCode(code: String): Boolean {
        return code.length == 6 && code.all { it.isDigit() }
    }
}