package com.cs407.budgetbuddy.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.util.Patterns
import com.cs407.budgetbuddy.data.repository.UserRepository

class ResetPasswordViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _resetState = MutableLiveData<ResetPasswordState>(ResetPasswordState.Initial)
    val resetState: LiveData<ResetPasswordState> = _resetState

    fun requestPasswordReset(email: String) {
        if (!isValidEmail(email)) {
            _resetState.value = ResetPasswordState.Error("Please enter a valid email address")
            return
        }

        viewModelScope.launch {
            try {
                _resetState.value = ResetPasswordState.Loading
                val success = userRepository.requestPasswordReset(email)
                if (success) {
                    _resetState.value = ResetPasswordState.VerificationSent(email)
                } else {
                    _resetState.value = ResetPasswordState.Error("Failed to send reset email")
                }
            } catch (e: Exception) {
                _resetState.value = ResetPasswordState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && 
               Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

sealed class ResetPasswordState {
    object Initial : ResetPasswordState()
    object Loading : ResetPasswordState()
    data class VerificationSent(val email: String) : ResetPasswordState()
    data class Error(val message: String) : ResetPasswordState()
}