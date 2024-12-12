package com.cs407.budgetbuddy.ui.settings

import android.content.Intent

/**
 * Sealed class representing different events that can occur in the settings screen
 */
sealed class SettingsEvent {
    object NavigateToLogin : SettingsEvent()
    data class ShowError(val message: String) : SettingsEvent()
    data class ShowSuccess(val message: String) : SettingsEvent()
    data class OpenEmail(val intent: Intent) : SettingsEvent()
}