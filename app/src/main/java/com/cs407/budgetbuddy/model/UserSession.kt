package com.cs407.budgetbuddy.model

import java.time.Instant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class UserSession(
    val userId: Long,
    val username: String,
    val createdAt: Instant = Instant.now(),
    val lastActivity: Instant = Instant.now()
) {
    fun isValid(): Boolean {
        val now = Instant.now()
        val sessionDuration = java.time.Duration.between(createdAt, now)
        val inactivityDuration = java.time.Duration.between(lastActivity, now)

        return sessionDuration.toHours() < 24 && // Session expires after 24 hours
                inactivityDuration.toMinutes() < 30 // Session expires after 30 minutes of inactivity
    }

    fun updateLastActivity(): UserSession {
        return copy(lastActivity = Instant.now())
    }
}

object UserSessionManager {
    private val _currentSession = MutableStateFlow<UserSession?>(null)
    val currentSession: StateFlow<UserSession?> = _currentSession

    fun updateSession(session: UserSession?) {
        _currentSession.value = session
    }

    fun clearSession() {
        _currentSession.value = null
    }
}