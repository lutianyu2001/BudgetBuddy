package com.cs407.budgetbuddy.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

/**
 * Utility class for date operations
 */
object DateUtil {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val monthFormatter = DateTimeFormatter.ofPattern("MM")
    private val dayFormatter = DateTimeFormatter.ofPattern("dd")
    
    /**
     * Gets the number of days in a given month
     */
    fun getDaysInMonth(year: Int, month: Int): Int {
        return YearMonth.of(year, month).lengthOfMonth()
    }

    /**
     * Formats date components into a string
     */
    fun formatDate(year: Int, month: Int, day: Int): String {
        return LocalDate.of(year, month, day).format(dateFormatter)
    }

    /**
     * Gets formatted month number (01-12)
     */
    fun formatMonth(month: Int): String {
        return String.format("%02d", month)
    }

    /**
     * Gets formatted day number (01-31)
     */
    fun formatDay(day: Int): String {
        return String.format("%02d", day)
    }

    /**
     * Gets labels for week chart (e.g., "12-25" format)
     */
    fun getWeekLabels(): Array<String> {
        val today = LocalDate.now()
        val monday = today.with(DayOfWeek.MONDAY)
        
        return Array(7) { index ->
            val date = monday.plusDays(index.toLong())
            "${formatMonth(date.monthValue)}-${formatDay(date.dayOfMonth)}"
        }
    }

    /**
     * Gets labels for month chart (1-31)
     */
    fun getMonthLabels(): Array<String> {
        val daysInMonth = LocalDate.now().lengthOfMonth()
        return Array(daysInMonth) { index -> 
            formatDay(index + 1)
        }
    }

    /**
     * Gets labels for year chart (Jan-Dec)
     */
    fun getYearLabels(): Array<String> {
        return Array(12) { index ->
            "Month ${index + 1}"
        }
    }

    /**
     * Gets day of week name in English
     */
    fun getDayOfWeekName(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            1 -> "Sunday"
            2 -> "Monday"
            3 -> "Tuesday"
            4 -> "Wednesday"
            5 -> "Thursday"
            6 -> "Friday"
            7 -> "Saturday"
            else -> "Unknown"
        }
    }
}