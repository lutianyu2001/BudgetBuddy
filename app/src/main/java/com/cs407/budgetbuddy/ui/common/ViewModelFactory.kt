package com.cs407.budgetbuddy.ui.common

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cs407.budgetbuddy.di.RepositoryModule
import com.cs407.budgetbuddy.ui.analysis.AnalysisViewModel
import com.cs407.budgetbuddy.ui.home.HomeViewModel
import com.cs407.budgetbuddy.ui.settings.SettingsViewModel
import com.cs407.budgetbuddy.ui.auth.LoginViewModel
import com.cs407.budgetbuddy.ui.auth.SignUpViewModel
import com.cs407.budgetbuddy.ui.auth.ResetPasswordViewModel
import com.cs407.budgetbuddy.ui.auth.EmailVerificationViewModel
import com.cs407.budgetbuddy.ui.transaction.AddTransactionViewModel
import com.cs407.budgetbuddy.ui.transaction.TransactionDetailViewModel
import com.cs407.budgetbuddy.ui.main.MainViewModel

/**
 * Factory for creating ViewModels with dependencies
 */
class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AnalysisViewModel::class.java) -> {
                AnalysisViewModel(
                    RepositoryModule.provideTransactionRepository(context)
                ) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(
                    RepositoryModule.provideTransactionRepository(context)
                ) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(
                    RepositoryModule.provideUserRepository(context),
                    RepositoryModule.provideTransactionRepository(context),
                    RepositoryModule.providePreferencesManager(context)
                ) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(
                    RepositoryModule.provideUserRepository(context)
                ) as T
            }
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(
                    RepositoryModule.provideUserRepository(context)
                ) as T
            }
            modelClass.isAssignableFrom(ResetPasswordViewModel::class.java) -> {
                ResetPasswordViewModel(
                    RepositoryModule.provideUserRepository(context)
                ) as T
            }
            modelClass.isAssignableFrom(EmailVerificationViewModel::class.java) -> {
                EmailVerificationViewModel(
                    RepositoryModule.provideUserRepository(context)
                ) as T
            }
            modelClass.isAssignableFrom(AddTransactionViewModel::class.java) -> {
                AddTransactionViewModel(
                    RepositoryModule.provideTransactionRepository(context)
                ) as T
            }
            modelClass.isAssignableFrom(TransactionDetailViewModel::class.java) -> {
                TransactionDetailViewModel(
                    RepositoryModule.provideTransactionRepository(context)
                ) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(
                    RepositoryModule.provideUserRepository(context)
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}