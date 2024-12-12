package com.cs407.budgetbuddy.ui.auth

/**
 * Sealed class representing different states of email verification
 */
sealed class VerificationState {
    object Initial : VerificationState()
    object Loading : VerificationState()
    object Success : VerificationState()
    data class Error(val message: String) : VerificationState()
}

/**
 * Data class for verification request result
 */
data class VerificationResult(
    val success: Boolean,
    val message: String? = null
)