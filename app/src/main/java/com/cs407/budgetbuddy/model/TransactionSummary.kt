package com.cs407.budgetbuddy.model

/**
 * Data class representing a summary of transactions
 */
data class TransactionSummary(
    val totalIncome: Double,
    val totalExpenses: Double,
    val balance: Double
)