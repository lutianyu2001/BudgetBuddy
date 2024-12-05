package com.cs407.budgetbuddy
import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class UserState(
    val id: Int = 0,
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val phoneNumber: String = ""
)

class UserViewModel : ViewModel() {
    val _userState = MutableStateFlow(UserState())
    val userState = _userState.asStateFlow()

    fun setUser(state: UserState) {
        _userState.update { currentState ->
            currentState.copy(id = state.id,
                username = state.username,
                email = state.email,
                password = state.password)
        }
    }
}