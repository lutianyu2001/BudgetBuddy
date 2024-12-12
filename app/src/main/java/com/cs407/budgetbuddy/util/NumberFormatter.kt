package com.cs407.budgetbuddy.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * Utility class for number formatting operations
 */
object NumberFormatter {
    private val currencyFormat = DecimalFormat("0.00")
    
    /**
     * Formats a double value to 2 decimal places
     * @param value Double value to format
     * @return Formatted string with 2 decimal places
     */
    fun formatCurrency(value: Double): String {
        val rounded = BigDecimal(value).setScale(2, RoundingMode.HALF_UP)
        return currencyFormat.format(rounded)
    }
}