package com.cs407.budgetbuddy.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.budgetbuddy.data.PreferencesManager
import com.cs407.budgetbuddy.data.repository.TransactionRepository
import com.cs407.budgetbuddy.data.repository.UserRepository
import com.cs407.budgetbuddy.model.UserProfile
import com.cs407.budgetbuddy.model.TransactionSummary
import com.cs407.budgetbuddy.model.TransactionType
import kotlinx.coroutines.launch

/**
 * ViewModel for the SettingsFragment
 */
class SettingsViewModel(
    private val userRepository: UserRepository,
    private val transactionRepository: TransactionRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> = _userProfile

    private val _financialSummary = MutableLiveData<TransactionSummary>()
    val financialSummary: LiveData<TransactionSummary> = _financialSummary

    private val _events = MutableLiveData<SettingsEvent>()
    val events: LiveData<SettingsEvent> = _events

    init {
        loadUserProfile()
        loadFinancialSummary()
    }

    fun connectBankAccount() {
        viewModelScope.launch {
            try {
//                val result = userRepository.connectBankAccount()
                val result = true
                if (result) {
                    _events.value = SettingsEvent.ShowSuccess("Bank account connected successfully")
                    loadUserProfile()
                } else {
                    _events.value = SettingsEvent.ShowError("Failed to connect bank account")
                }
            } catch (e: Exception) {
                _events.value = SettingsEvent.ShowError(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun syncBankTransactions() {
        viewModelScope.launch {
            try {
//                val count = transactionRepository.syncBankTransactions()
                val count = 0
                _events.value = SettingsEvent.ShowSuccess("Synced $count transactions")
                loadFinancialSummary()
            } catch (e: Exception) {
                _events.value = SettingsEvent.ShowError(e.message ?: "Failed to sync transactions")
            }
        }
    }

    fun removeBankAccount() {
        viewModelScope.launch {
            try {
//                userRepository.removeBankAccount()
                _events.value = SettingsEvent.ShowSuccess("Bank account removed")
                loadUserProfile()
            } catch (e: Exception) {
                _events.value = SettingsEvent.ShowError(e.message ?: "Failed to remove bank account")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                userRepository.logout()
                preferencesManager.clearUserData()
                _events.value = SettingsEvent.NavigateToLogin
            } catch (e: Exception) {
                _events.value = SettingsEvent.ShowError(e.message ?: "Failed to logout")
            }
        }
    }

    fun showFAQ() {
        // In a real app, navigate to FAQ screen or open web FAQ
    }

    fun contactSupport() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:support@budgetbuddy.com")
            putExtra(Intent.EXTRA_SUBJECT, "BudgetBuddy Support Request")
        }
        _events.value = SettingsEvent.OpenEmail(intent)
    }

    fun reportIssue() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:support@budgetbuddy.com")
            putExtra(Intent.EXTRA_SUBJECT, "BudgetBuddy Issue Report")
            putExtra(Intent.EXTRA_TEXT, """
                Please provide the following information:
                
                Device:
                Android Version:
                App Version:
                
                Issue Description:
                
            """.trimIndent())
        }
        _events.value = SettingsEvent.OpenEmail(intent)
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                _userProfile.value = userRepository.getUserProfile()
            } catch (e: Exception) {
                _events.value = SettingsEvent.ShowError(e.message ?: "Failed to load profile")
            }
        }
    }

    private fun loadFinancialSummary() {
        viewModelScope.launch {
            try {
                val transactions = transactionRepository.getAllTransactions()
                val income = transactions
                    .filter { it.type == TransactionType.INCOME }
                    .sumOf { it.amount }
                val expenses = transactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount }

                _financialSummary.value = TransactionSummary(
                    totalIncome = income,
                    totalExpenses = expenses,
                    balance = income - expenses
                )
            } catch (e: Exception) {
                _events.value = SettingsEvent.ShowError(e.message ?: "Failed to load summary")
            }
        }
    }
}