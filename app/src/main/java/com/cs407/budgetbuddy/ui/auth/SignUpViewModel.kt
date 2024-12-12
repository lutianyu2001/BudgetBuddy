package com.cs407.budgetbuddy.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.budgetbuddy.data.repository.UserRepository
import com.cs407.budgetbuddy.model.SignUpForm
import com.cs407.budgetbuddy.model.SignUpState
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val userRepository: UserRepository  // Constructor parameter needed
) : ViewModel() {

    private val _signUpState = MutableLiveData<SignUpState>(SignUpState.Initial)
    val signUpState: LiveData<SignUpState> = _signUpState

    fun signUp(form: SignUpForm) {
        form.validate()?.let { error ->
            _signUpState.value = SignUpState.Error(error)
            return
        }

        viewModelScope.launch {
            try {
                _signUpState.value = SignUpState.Loading
                val success = userRepository.createAccount(
                    username = form.username,
                    email = form.email,
                    password = form.password
                )
                if (success) {
                    _signUpState.value = SignUpState.Success
                } else {
                    _signUpState.value = SignUpState.Error("Failed to create account")
                }
            } catch (e: Exception) {
                _signUpState.value = SignUpState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}