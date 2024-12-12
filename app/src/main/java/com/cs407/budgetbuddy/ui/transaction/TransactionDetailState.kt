package com.cs407.budgetbuddy.ui.transaction

/**
 * Sealed class representing different states of the transaction detail screen
 */
sealed class TransactionDetailState {
    object Initial : TransactionDetailState()
    object Loading : TransactionDetailState()
    data class Success(
        val message: String? = null,
        val finish: Boolean = false
    ) : TransactionDetailState()
    data class Error(val message: String) : TransactionDetailState()
}