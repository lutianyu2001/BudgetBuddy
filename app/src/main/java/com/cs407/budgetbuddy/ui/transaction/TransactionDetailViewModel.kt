package com.cs407.budgetbuddy.ui.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.budgetbuddy.data.repository.TransactionRepository
import com.cs407.budgetbuddy.model.Transaction
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class TransactionDetailViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _transaction = MutableLiveData<Transaction>()
    val transaction: LiveData<Transaction> = _transaction

    private val _state = MutableLiveData<TransactionDetailState>(TransactionDetailState.Initial)
    val state: LiveData<TransactionDetailState> = _state

    private var currentTransactionId: Long = -1

    fun loadTransaction(transactionId: Long) {
        currentTransactionId = transactionId
        viewModelScope.launch {
            try {
                _state.value = TransactionDetailState.Loading
                val transaction = repository.getTransaction(transactionId)
                if (transaction != null) {
                    _transaction.value = transaction
                    _state.value = TransactionDetailState.Success()
                } else {
                    _state.value = TransactionDetailState.Error("Transaction not found")
                }
            } catch (e: Exception) {
                _state.value = TransactionDetailState.Error(e.message ?: "Failed to load transaction")
            }
        }
    }

    fun updateTransaction(
        amount: Double,
        details: String,
        store: String,
        comments: String
    ) {
        val currentTransaction = _transaction.value ?: return
        
        if (!validateInput(amount, details)) return

        val updatedTransaction = currentTransaction.copy(
            amount = amount,
            details = details,
            store = store,
            comments = comments
        )

        viewModelScope.launch {
            try {
                _state.value = TransactionDetailState.Loading
                repository.updateTransaction(updatedTransaction)
                _transaction.value = updatedTransaction
                _state.value = TransactionDetailState.Success(
                    message = "Transaction updated successfully"
                )
            } catch (e: Exception) {
                _state.value = TransactionDetailState.Error(
                    e.message ?: "Failed to update transaction"
                )
            }
        }
    }

    fun deleteTransaction() {
        viewModelScope.launch {
            try {
                _state.value = TransactionDetailState.Loading
                repository.deleteTransaction(currentTransactionId)
                _state.value = TransactionDetailState.Success(
                    message = "Transaction deleted",
                    finish = true
                )
            } catch (e: Exception) {
                _state.value = TransactionDetailState.Error(
                    e.message ?: "Failed to delete transaction"
                )
            }
        }
    }

    fun setSelectedDate(date: LocalDateTime) {
        _transaction.value?.let { current ->
            _transaction.value = current.copy(date = date)
        }
    }

    fun setSelectedCategory(category: String) {
        _transaction.value?.let { current ->
            _transaction.value = current.copy(category = category)
        }
    }

    private fun validateInput(amount: Double, details: String): Boolean {
        when {
            amount <= 0 -> {
                _state.value = TransactionDetailState.Error("Please enter a valid amount")
                return false
            }
            details.isBlank() -> {
                _state.value = TransactionDetailState.Error("Please enter transaction details")
                return false
            }
        }
        return true
    }
}