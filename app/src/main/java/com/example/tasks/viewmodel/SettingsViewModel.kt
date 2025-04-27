package com.example.tasks.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasks.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
): ViewModel() {

    val theme: StateFlow<String> = settingsRepository.getTheme()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")

    val sortOrder: StateFlow<String> = settingsRepository.getSortOrder()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "exp. soon")

    val showExpired: StateFlow<Boolean> = settingsRepository.getShowExpired()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val dailyReminderEnabled: StateFlow<Boolean> = settingsRepository.getDailyReminder()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val confirmDelete: StateFlow<Boolean> = settingsRepository.getConfirmDelete()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun updateTheme(theme: String) {
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
        }
    }

    fun updateSortOrder(sortOrder: String) {
        viewModelScope.launch {
            settingsRepository.setSortOrder(sortOrder)
        }
    }

    fun updateShowExpired(value: Boolean) {
        viewModelScope.launch {
            settingsRepository.setShowExpired(value)
        }
    }

    fun updateDailyReminder(enabled: Boolean, context: Context) {
        viewModelScope.launch {
            settingsRepository.setDailyReminder(enabled)
            val sharedPrefs = context.getSharedPreferences("user_settings", Context.MODE_PRIVATE)
            sharedPrefs.edit().putBoolean("daily_reminder", enabled).apply()
        }
    }

    fun updateConfirmDelete(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setConfirmDelete(enabled)
        }
    }
}