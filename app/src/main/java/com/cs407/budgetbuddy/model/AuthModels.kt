package com.cs407.budgetbuddy.model

/**
 * Data class representing saved login credentials
 */
data class Credentials(
    val username: String = "",
    val password: String = "",
    val rememberMe: Boolean = false
)

/**
 * Sealed class representing different states of the login process
 */
sealed class LoginState {
    object Initial : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

/**
 * Sealed class representing different states of the sign up process
 */
sealed class SignUpState {
    object Initial : SignUpState()
    object Loading : SignUpState()
    object Success : SignUpState()
    data class Error(val message: String) : SignUpState()
}

/**
 * Data class representing sign up form data
 */
data class SignUpForm(
    val username: String,
    val email: String,
    val password: String,
    val confirmPassword: String
) {
    fun validate(): String? {
        return when {
            username.length < 3 -> "Username must be at least 3 characters"
            !email.contains("@") -> "Invalid email address"
            password.length < 8 -> "Password must be at least 8 characters"
            password != confirmPassword -> "Passwords do not match"
            else -> null
        }
    }
}