package com.cs407.budgetbuddy.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.budgetbuddy.data.repository.UserRepository
import com.cs407.budgetbuddy.model.UserProfile
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> = _userProfile

    private val _events = MutableLiveData<EditProfileEvent>()
    val events: LiveData<EditProfileEvent> = _events

    init {
        loadUserProfile()
    }

    fun updateProfile(username: String, email: String) {
        if (username.isBlank()) {
            _events.value = EditProfileEvent.ShowError("Username cannot be empty")
            return
        }

        viewModelScope.launch {
            try {
                _userProfile.value?.let { currentProfile ->
                    val updatedProfile = currentProfile.copy(
                        username = username,
                        email = email.takeIf { it.isNotBlank() }
                    )
                    userRepository.updateUserProfile(updatedProfile)
                    _events.value = EditProfileEvent.ShowSuccess("Profile updated successfully")
                }
            } catch (e: Exception) {
                _events.value = EditProfileEvent.ShowError(e.message ?: "Failed to update profile")
            }
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                userRepository.getUserProfile()?.let {
                    _userProfile.value = it
                }
            } catch (e: Exception) {
                _events.value = EditProfileEvent.ShowError(e.message ?: "Failed to load profile")
            }
        }
    }
}

sealed class EditProfileEvent {
    data class ShowError(val message: String) : EditProfileEvent()
    data class ShowSuccess(val message: String) : EditProfileEvent()
}