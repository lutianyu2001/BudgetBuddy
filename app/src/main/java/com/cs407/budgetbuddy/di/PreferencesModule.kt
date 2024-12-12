package com.cs407.budgetbuddy.di

import android.content.Context
import com.cs407.budgetbuddy.data.PreferencesManager

/**
 * Dependency injection module for preferences management
 */
object PreferencesModule {
    private var testPreferencesManager: PreferencesManager? = null

    fun providePreferencesManager(context: Context): PreferencesManager {
        return testPreferencesManager ?: PreferencesManager.getInstance(context)
    }

    /**
     * Used for testing to inject a mock PreferencesManager
     */
    @JvmStatic
    fun setTestPreferencesManager(preferencesManager: PreferencesManager?) {
        testPreferencesManager = preferencesManager
    }
}