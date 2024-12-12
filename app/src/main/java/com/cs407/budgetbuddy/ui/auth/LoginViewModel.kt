package com.cs407.budgetbuddy.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.budgetbuddy.data.repository.UserRepository
import com.cs407.budgetbuddy.model.Credentials
import com.cs407.budgetbuddy.model.LoginState
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loginState = MutableLiveData<LoginState>(LoginState.Initial)
    val loginState: LiveData<LoginState> = _loginState

    private val _savedCredentials = MutableLiveData<Credentials>()
    val savedCredentials: LiveData<Credentials> = _savedCredentials

    init {
        loadSavedCredentials()
    }

    fun login(username: String, password: String) {
        when {
            username.isEmpty() -> {
                _loginState.value = LoginState.Error("Username cannot be empty")
                return
            }
            password.isEmpty() -> {
                _loginState.value = LoginState.Error("Password cannot be empty")
                return
            }
        }

        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading
                val success = userRepository.login(username, password)
                if (success) {
                    // Save credentials if remember me is enabled
                    userRepository.saveCredentials(username, password)
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error("Invalid username or password")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    // Add method to handle remember me toggle
    fun setRememberMe(enabled: Boolean) {
        viewModelScope.launch {
            userRepository.setRememberMe(enabled)
        }
    }

    private fun loadSavedCredentials() {
        viewModelScope.launch {
            _savedCredentials.value = userRepository.getSavedCredentials()
        }
    }
}