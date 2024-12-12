package com.cs407.budgetbuddy.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

/**
 * Manager class for handling shared preferences storage
 * Uses EncryptedSharedPreferences for sensitive data
 */
class PreferencesManager private constructor(context: Context) {

    private val regularPrefs: SharedPreferences = context.getSharedPreferences(
        PREFS_REGULAR_NAME,
        Context.MODE_PRIVATE
    )

    private val securePrefs: SharedPreferences = createEncryptedSharedPreferences(context)

    /**
     * User Session Management
     */
    fun setCurrentUserId(userId: Long) {
        securePrefs.edit {
            putLong(KEY_CURRENT_USER_ID, userId)
        }
    }

    fun getCurrentUserId(): Long? {
        return if (securePrefs.contains(KEY_CURRENT_USER_ID)) {
            securePrefs.getLong(KEY_CURRENT_USER_ID, -1)
        } else {
            null
        }
    }

    /**
     * Remember Me Functionality
     */
    fun setRememberMe(enabled: Boolean) {
        regularPrefs.edit {
            putBoolean(KEY_REMEMBER_ME, enabled)
        }
    }

    fun getRememberMe(): Boolean {
        return regularPrefs.getBoolean(KEY_REMEMBER_ME, false)
    }

    fun setSavedUsername(username: String) {
        if (getRememberMe()) {
            securePrefs.edit {
                putString(KEY_SAVED_USERNAME, username)
            }
        }
    }

    fun getSavedUsername(): String? {
        return if (getRememberMe()) {
            securePrefs.getString(KEY_SAVED_USERNAME, null)
        } else {
            null
        }
    }

    fun setSavedPassword(password: String) {
        if (getRememberMe()) {
            securePrefs.edit {
                putString(KEY_SAVED_PASSWORD, password)
            }
        }
    }

    fun getSavedPassword(): String? {
        return if (getRememberMe()) {
            securePrefs.getString(KEY_SAVED_PASSWORD, null)
        } else {
            null
        }
    }

    /**
     * App Settings
     */
    fun setFirstLaunch(isFirst: Boolean) {
        regularPrefs.edit {
            putBoolean(KEY_FIRST_LAUNCH, isFirst)
        }
    }

    fun isFirstLaunch(): Boolean {
        return regularPrefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun setDarkMode(enabled: Boolean) {
        regularPrefs.edit {
            putBoolean(KEY_DARK_MODE, enabled)
        }
    }

    fun isDarkMode(): Boolean {
        return regularPrefs.getBoolean(KEY_DARK_MODE, false)
    }

    /**
     * Bank Integration
     */
    fun setBankAccountConnected(connected: Boolean) {
        regularPrefs.edit {
            putBoolean(KEY_BANK_CONNECTED, connected)
        }
    }

    fun isBankAccountConnected(): Boolean {
        return regularPrefs.getBoolean(KEY_BANK_CONNECTED, false)
    }

    fun setLastSyncTime(timestamp: Long) {
        regularPrefs.edit {
            putLong(KEY_LAST_SYNC, timestamp)
        }
    }

    fun getLastSyncTime(): Long {
        return regularPrefs.getLong(KEY_LAST_SYNC, 0)
    }

    /**
     * Data Management
     */
    fun clearUserData() {
        securePrefs.edit { clear() }
        regularPrefs.edit {
            remove(KEY_REMEMBER_ME)
            remove(KEY_BANK_CONNECTED)
            remove(KEY_LAST_SYNC)
        }
    }

    private fun createEncryptedSharedPreferences(context: Context): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        return EncryptedSharedPreferences.create(
            PREFS_SECURE_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    companion object {
        // Preference file names
        private const val PREFS_REGULAR_NAME = "BudgetBuddy.preferences"
        private const val PREFS_SECURE_NAME = "BudgetBuddy.secure_preferences"

        // Regular preference keys
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_BANK_CONNECTED = "bank_connected"
        private const val KEY_LAST_SYNC = "last_sync"

        // Secure preference keys
        private const val KEY_CURRENT_USER_ID = "current_user_id"
        private const val KEY_SAVED_USERNAME = "saved_username"
        private const val KEY_SAVED_PASSWORD = "saved_password"

        @Volatile
        private var instance: PreferencesManager? = null

        fun getInstance(context: Context): PreferencesManager {
            return instance ?: synchronized(this) {
                instance ?: PreferencesManager(context.applicationContext).also { 
                    instance = it 
                }
            }
        }
    }
}