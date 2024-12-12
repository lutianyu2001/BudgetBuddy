package com.cs407.budgetbuddy.ui.transaction

/**
 * Sealed class representing different states of transaction saving process
 */
sealed class TransactionSaveState {
    object Initial : TransactionSaveState()
    object Loading : TransactionSaveState()
    object Success : TransactionSaveState()
    data class Error(val message: String) : TransactionSaveState()
}