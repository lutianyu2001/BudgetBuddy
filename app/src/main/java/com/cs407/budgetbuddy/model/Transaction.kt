package com.cs407.budgetbuddy.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Represents the type of transaction
 */
enum class TransactionType(val value: Int) {
    INCOME(1),
    EXPENSE(2);

    companion object {
        fun fromInt(value: Int) = values().first { it.value == value }
    }
}

/**
 * Data class representing a financial transaction in the BudgetBuddy application
 */
@Parcelize
data class Transaction(
    val id: Long = 0,
    val userId: Long = 0,
    val account: String,
    val details: String,
    val amount: Double,
    val store: String,
    val category: String,
    val date: LocalDateTime,
    val comments: String? = null,
    val type: TransactionType,
) : Parcelable {

    /**
     * Formats the amount with currency symbol
     */
    fun getFormattedAmount(): String {
        val prefix = if (type == TransactionType.INCOME) "+" else "-"
        return "$prefix$${String.format("%.2f", amount)}"
    }

    /**
     * Formats the date for display
     */
    fun getFormattedDate(pattern: String = "MMM dd, yyyy"): String {
        return date.format(DateTimeFormatter.ofPattern(pattern))
    }

    /**
     * Validates if the transaction data is valid
     * @return Pair of Boolean and error message (null if valid)
     */
    fun validate(): Pair<Boolean, String?> {
        return when {
            amount <= 0 -> Pair(false, "Amount must be greater than 0")
            category.isBlank() -> Pair(false, "Category is required")
            details.isBlank() -> Pair(false, "Details are required")
            else -> Pair(true, null)
        }
    }
}