package com.cs407.budgetbuddy.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Data class representing a user in the BudgetBuddy application
 * @property id Unique identifier for the user
 * @property username Username for login
 * @property password Encrypted password (not to be exposed in UI)
 * @property email User's email address
 * @property bankAccount Optional bank account number
 * @property bankPassword Optional encrypted bank password
 */
@Parcelize
data class User(
    val id: Long = 0,
    val username: String,
    val password: String,
    val email: String? = null,
    val bankAccount: String? = null,
    val bankPassword: String? = null
) : Parcelable {
    
    companion object {
        const val MIN_PASSWORD_LENGTH = 8
        const val MIN_USERNAME_LENGTH = 3
    }

    /**
     * Validates if the user data meets basic requirements
     * @return Pair of Boolean and error message (null if valid)
     */
    fun validate(): Pair<Boolean, String?> {
        return when {
            username.length < MIN_USERNAME_LENGTH -> 
                Pair(false, "Username must be at least $MIN_USERNAME_LENGTH characters")
            password.length < MIN_PASSWORD_LENGTH -> 
                Pair(false, "Password must be at least $MIN_PASSWORD_LENGTH characters")
            !email.isNullOrEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> 
                Pair(false, "Invalid email address")
            else -> Pair(true, null)
        }
    }

    /**
     * Creates a copy of the user with sensitive data masked
     * @return User object with sensitive fields masked
     */
    fun maskSensitiveData(): User {
        return copy(
            password = "********",
            bankPassword = bankPassword?.let { "********" }
        )
    }
}