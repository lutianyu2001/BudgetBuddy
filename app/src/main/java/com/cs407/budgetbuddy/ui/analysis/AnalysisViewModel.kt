package com.cs407.budgetbuddy.ui.analysis

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.budgetbuddy.data.repository.TransactionRepository
import com.cs407.budgetbuddy.model.ChartTimeFrame
import com.cs407.budgetbuddy.model.LineChartData
import com.cs407.budgetbuddy.model.PieChartData
import com.cs407.budgetbuddy.model.Transaction
import com.cs407.budgetbuddy.model.TransactionSummary
import com.cs407.budgetbuddy.model.TransactionType
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AnalysisViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _selectedTimeFrame = MutableLiveData<ChartTimeFrame>(ChartTimeFrame.WEEK)
    val selectedTimeFrame: LiveData<ChartTimeFrame> = _selectedTimeFrame

    private val _lineChartData = MutableLiveData<LineChartData>()
    val lineChartData: LiveData<LineChartData> = _lineChartData

    private val _pieChartData = MutableLiveData<PieChartData>()
    val pieChartData: LiveData<PieChartData> = _pieChartData

    private val _summary = MutableLiveData<TransactionSummary>()
    val summary: LiveData<TransactionSummary> = _summary

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadData()
    }

    fun setTimeFrame(timeFrame: ChartTimeFrame) {
        _selectedTimeFrame.value = timeFrame
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                when (_selectedTimeFrame.value) {
                    ChartTimeFrame.WEEK -> loadWeekData()
                    ChartTimeFrame.MONTH -> loadMonthData()
                    ChartTimeFrame.YEAR -> loadYearData()
                    else -> loadWeekData()
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadWeekData() {
        val today = LocalDate.now()
        val startOfWeek = today.minusDays(6)
        val dailyExpenses = repository.getDailyExpenseTotals(startOfWeek, today)

        // Create arrays for values and labels
        val values = mutableListOf<Double>()
        val labels = mutableListOf<String>()

        // Fill in data for each day
        var currentDate = startOfWeek
        while (!currentDate.isAfter(today)) {
            values.add(dailyExpenses[currentDate] ?: 0.0)
            labels.add(currentDate.format(DateTimeFormatter.ofPattern("MM-dd")))
            currentDate = currentDate.plusDays(1)
        }

        _lineChartData.postValue(LineChartData(values, labels.toTypedArray()))

        // Update summary for the week
        updateSummary(repository.getTransactionsForDateRange(
            startOfWeek.atStartOfDay(),
            today.plusDays(1).atStartOfDay()
        ))
    }

    private suspend fun loadMonthData() {
        val now = LocalDate.now()
        val transactions = repository.getTransactionsForMonth(now.year, now.monthValue)

        updatePieChartData(transactions)
        updateSummary(transactions)
    }

    private suspend fun loadYearData() {
        val currentYear = LocalDate.now().year
        val startDate = LocalDate.of(currentYear, 1, 1).atStartOfDay()
        val endDate = LocalDate.of(currentYear, 12, 31).atStartOfDay()

        val transactions = repository.getTransactionsForDateRange(startDate, endDate)

        updatePieChartData(transactions)
        updateSummary(transactions)
    }

    private fun updatePieChartData(transactions: List<Transaction>) {
        val categoryTotals = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { (_, transactions) ->
                transactions.sumOf { it.amount }
            }

        val total = categoryTotals.values.sum()
        val percentages = categoryTotals.values.map {
            (it / total) * 100
        }

        _pieChartData.value = PieChartData(
            categories = categoryTotals.keys.toList(),
            values = categoryTotals.values.toList(),
            percentages = percentages
        )
    }

    private fun updateSummary(transactions: List<Transaction>) {
        val income = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

        val expenses = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        _summary.value = TransactionSummary(
            totalIncome = income,
            totalExpenses = expenses,
            balance = income - expenses
        )
    }

    fun refreshData() {
        loadData()
    }
}