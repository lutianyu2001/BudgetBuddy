package com.cs407.budgetbuddy.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.budgetbuddy.model.Transaction
import kotlinx.coroutines.launch
import com.cs407.budgetbuddy.data.repository.TransactionRepository
import com.cs407.budgetbuddy.model.TransactionSummary
import com.cs407.budgetbuddy.model.TransactionType

class HomeViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions

    private val _summary = MutableLiveData<TransactionSummary>()
    val summary: LiveData<TransactionSummary> = _summary

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // Store the original transaction list for filtering
    private var allTransactions: List<Transaction> = emptyList()

    init {
        loadTransactions()
    }

    fun refreshData() {
        loadTransactions()
    }

    fun searchTransactions(query: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // If query is empty, restore all transactions
                if (query.isBlank()) {
                    _transactions.value = allTransactions
                    updateSummary(allTransactions)
                    return@launch
                }

                // Search in both category and details
                val filteredTransactions = allTransactions.filter { transaction ->
                    transaction.category.contains(query, ignoreCase = true) ||
                            transaction.details.contains(query, ignoreCase = true)
                }

                _transactions.value = filteredTransactions
                updateSummary(filteredTransactions)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getTransactionAt(position: Int): Transaction? {
        return transactions.value?.getOrNull(position)
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                allTransactions = repository.getAllTransactions()
                _transactions.value = allTransactions
                updateSummary(allTransactions)
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun updateSummary(transactions: List<Transaction>) {
        var totalIncome = 0.0
        var totalExpenses = 0.0

        transactions.forEach { transaction ->
            when (transaction.type) {
                TransactionType.INCOME -> totalIncome += transaction.amount
                TransactionType.EXPENSE -> totalExpenses += transaction.amount
            }
        }

        _summary.value = TransactionSummary(
            totalIncome = totalIncome,
            totalExpenses = totalExpenses,
            balance = totalIncome - totalExpenses
        )
    }
}