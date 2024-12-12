package com.cs407.budgetbuddy.ui.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.budgetbuddy.data.repository.TransactionRepository
import com.cs407.budgetbuddy.model.Transaction
import com.cs407.budgetbuddy.model.TransactionType
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class AddTransactionViewModel(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories

    private val _selectedCategory = MutableLiveData<String>()
    val selectedCategory: LiveData<String> = _selectedCategory

    private val _selectedDate = MutableLiveData(LocalDateTime.now())
    val selectedDate: LiveData<LocalDateTime> = _selectedDate

    private val _transactionType = MutableLiveData<TransactionType>(TransactionType.EXPENSE)
    val transactionType: LiveData<TransactionType> = _transactionType

    private val _saveState = MutableLiveData<TransactionSaveState>(TransactionSaveState.Initial)
    val saveState: LiveData<TransactionSaveState> = _saveState

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                // In a real app, these would come from the repository
                _categories.value = listOf(
                    "Food",
                    "Transportation",
                    "Entertainment",
                    "Shopping",
                    "Bills",
                    "Others"
                )
            } catch (e: Exception) {
                _saveState.value = TransactionSaveState.Error("Failed to load categories")
            }
        }
    }

    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSelectedDate(date: LocalDateTime) {
        _selectedDate.value = date
    }

    fun setTransactionType(type: TransactionType) {
        _transactionType.value = type
    }

    fun saveTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                _saveState.value = TransactionSaveState.Loading
                val id = transactionRepository.addTransaction(transaction)
                if (id > 0) {
                    _saveState.value = TransactionSaveState.Success
                } else {
                    _saveState.value = TransactionSaveState.Error("Failed to save transaction")
                }
            } catch (e: Exception) {
                _saveState.value = TransactionSaveState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}