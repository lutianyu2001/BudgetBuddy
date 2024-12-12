package com.cs407.budgetbuddy.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.budgetbuddy.data.repository.UserRepository
import com.cs407.budgetbuddy.model.UserSession
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userSession = MutableLiveData<UserSession?>()
    val userSession: LiveData<UserSession?> = _userSession

    init {
        observeUserSession()
    }

    private fun observeUserSession() {
        viewModelScope.launch {
            userRepository.userSessionFlow.collect { session ->
                _userSession.value = session?.takeIf { it.isValid() }
            }
        }
    }
}