package com.cs407.budgetbuddy.model

/**
 * Data class representing a user's profile information
 */
data class UserProfile(
    val id: Long,
    val username: String,
    val email: String?,
    val hasBankAccount: Boolean,
    val bankAccountNumber: String?
) {
    val maskedBankAccount: String?
        get() = bankAccountNumber?.let { number ->
            "****" + number.takeLast(4)
        }
}