package com.cs407.budgetbuddy.di

import android.content.Context
import com.cs407.budgetbuddy.data.repository.TransactionRepository
import com.cs407.budgetbuddy.data.repository.UserRepository
import com.cs407.budgetbuddy.data.PreferencesManager

/**
 * Dependency injection module for repository management
 */
object RepositoryModule {
    private var transactionRepository: TransactionRepository? = null
    private var userRepository: UserRepository? = null
    private var preferencesManager: PreferencesManager? = null

    fun provideTransactionRepository(context: Context): TransactionRepository {
        return transactionRepository ?: synchronized(this) {
            transactionRepository ?: TransactionRepository.getInstance(context.applicationContext).also {
                transactionRepository = it
            }
        }
    }

    fun provideUserRepository(context: Context): UserRepository {
        return userRepository ?: synchronized(this) {
            userRepository ?: UserRepository.getInstance(context.applicationContext).also {
                userRepository = it
            }
        }
    }

    fun providePreferencesManager(context: Context): PreferencesManager {
        return preferencesManager ?: synchronized(this) {
            preferencesManager ?: PreferencesManager.getInstance(context.applicationContext).also {
                preferencesManager = it
            }
        }
    }

    /**
     * Used for testing to inject mock repositories
     */
    @JvmStatic
    fun setTestRepositories(
        testTransactionRepo: TransactionRepository? = null,
        testUserRepo: UserRepository? = null,
        testPreferencesManager: PreferencesManager? = null
    ) {
        transactionRepository = testTransactionRepo
        userRepository = testUserRepo
        preferencesManager = testPreferencesManager
    }

    /**
     * Clears all repository instances
     */
    fun clearRepositories() {
        transactionRepository = null
        userRepository = null
        preferencesManager = null
    }
}