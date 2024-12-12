package com.cs407.budgetbuddy.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.cs407.budgetbuddy.data.PreferencesManager
import com.cs407.budgetbuddy.db.DatabaseHelper
import com.cs407.budgetbuddy.model.Credentials
import com.cs407.budgetbuddy.model.User
import com.cs407.budgetbuddy.model.UserProfile
import com.cs407.budgetbuddy.model.UserSession
import com.cs407.budgetbuddy.model.UserSessionManager
import com.cs407.budgetbuddy.util.MD5Util
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository handling user-related data operations
 */
class UserRepository private constructor(context: Context) : Repository() {
    private val dbHelper = DatabaseHelper.getInstance(context)
    private val prefsManager = PreferencesManager.getInstance(context)

    val userSessionFlow: StateFlow<UserSession?> = UserSessionManager.currentSession

    override fun getDatabase() = dbHelper.writableDatabase

    suspend fun saveCredentials(username: String, password: String) {
        if (prefsManager.getRememberMe()) {
            prefsManager.setSavedUsername(username)
            prefsManager.setSavedPassword(password)
        } else {
            // Clear saved credentials if remember me is disabled
            prefsManager.setSavedUsername("")
            prefsManager.setSavedPassword("")
        }
    }

    suspend fun login(username: String, password: String): Boolean {
        val hashedPassword = MD5Util.generateMD5(password)
        return executeQuery(
            """
            SELECT * FROM ${DatabaseHelper.TABLE_USER}
            WHERE ${DatabaseHelper.COLUMN_USERNAME} = ? 
            AND ${DatabaseHelper.COLUMN_PASSWORD} = ?
            """,
            arrayOf(username, hashedPassword)
        ) { cursor ->
            if (cursor.moveToFirst()) {
                val userId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                prefsManager.setCurrentUserId(userId)

                // Create and update session
                val session = UserSession(
                    userId = userId,
                    username = username
                )
                UserSessionManager.updateSession(session)
                true
            } else {
                false
            }
        } ?: false
    }

    suspend fun createAccount(username: String, email: String, password: String): Boolean {
        // Check if username already exists
        val exists = executeQuery(
            "SELECT 1 FROM ${DatabaseHelper.TABLE_USER} WHERE ${DatabaseHelper.COLUMN_USERNAME} = ?",
            arrayOf(username)
        ) { it.moveToFirst() } ?: false

        if (exists) {
            throw DatabaseException("Username already exists")
        }

        return executeWrite { db ->
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_USERNAME, username)
                put(DatabaseHelper.COLUMN_PASSWORD, MD5Util.generateMD5(password))
                put(DatabaseHelper.COLUMN_EMAIL, email)
            }
            db.insert(DatabaseHelper.TABLE_USER, null, values)
        }.let { true }
    }

    suspend fun getUserProfile(): UserProfile? {
        val userId = prefsManager.getCurrentUserId() ?: return null
        return executeQuery(
            """
            SELECT * FROM ${DatabaseHelper.TABLE_USER}
            WHERE ${DatabaseHelper.COLUMN_ID} = ?
            """,
            arrayOf(userId.toString())
        ) { cursor -> cursor.toUserProfile() }
    }

    suspend fun updateUserProfile(profile: UserProfile): Boolean {
        return executeWrite { db ->
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_USERNAME, profile.username)
                put(DatabaseHelper.COLUMN_EMAIL, profile.email)
                put(DatabaseHelper.COLUMN_BANK_ACCOUNT, profile.bankAccountNumber)
            }
            db.update(
                DatabaseHelper.TABLE_USER,
                values,
                "${DatabaseHelper.COLUMN_ID} = ?",
                arrayOf(profile.id.toString())
            )
        }.let { true }
    }

    suspend fun setRememberMe(enabled: Boolean) {
        prefsManager.setRememberMe(enabled)
    }

    suspend fun getSavedCredentials(): Credentials {
        return if (prefsManager.getRememberMe()) {
            Credentials(
                username = prefsManager.getSavedUsername() ?: "",
                password = prefsManager.getSavedPassword() ?: "",
                rememberMe = true
            )
        } else {
            Credentials()
        }
    }

    suspend fun logout() {
        prefsManager.clearUserData()
        UserSessionManager.clearSession()
    }

    suspend fun requestPasswordReset(email: String): Boolean {
        // In a real app, this would connect to a backend service
        // For now, we'll just verify the email exists
        return executeQuery(
            """
            SELECT 1 FROM ${DatabaseHelper.TABLE_USER}
            WHERE ${DatabaseHelper.COLUMN_EMAIL} = ?
            """,
            arrayOf(email)
        ) { it.moveToFirst() } ?: false
    }

    private fun Cursor.toUserProfile(): UserProfile {
        return UserProfile(
            id = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
            username = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME)),
            email = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL)),
            hasBankAccount = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_BANK_ACCOUNT)) != null,
            bankAccountNumber = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_BANK_ACCOUNT))
        )
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(context: Context): UserRepository {
            return instance ?: synchronized(this) {
                instance ?: UserRepository(context).also { instance = it }
            }
        }
    }
}