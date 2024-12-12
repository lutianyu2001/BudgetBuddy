package com.cs407.budgetbuddy.model

/**
 * Represents different time frames for chart analysis
 */
enum class ChartTimeFrame {
    WEEK,
    MONTH,
    YEAR
}

/**
 * Data class for line chart data
 */
data class LineChartData(
    val values: List<Double>,
    val labels: Array<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LineChartData

        if (values != other.values) return false
        return labels.contentEquals(other.labels)
    }

    override fun hashCode(): Int {
        var result = values.hashCode()
        result = 31 * result + labels.contentHashCode()
        return result
    }
}

/**
 * Data class for pie chart data
 */
data class PieChartData(
    val categories: List<String>,
    val values: List<Double>,
    val percentages: List<Double>
) {
    init {
        require(categories.size == values.size && values.size == percentages.size) {
            "Categories, values, and percentages lists must have the same size"
        }
    }
}