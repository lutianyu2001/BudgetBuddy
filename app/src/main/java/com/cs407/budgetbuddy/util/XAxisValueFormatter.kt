package com.cs407.budgetbuddy.util

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class XAxisValueFormatter(private val values: Array<String>) : ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase?): String {
        return try {
            values[value.toInt()]
        } catch (e: Exception) {
            value.toString()
        }
    }
}