package com.example.tasks.repository

import com.example.tasks.datastore.DataStoreManager
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val dataStoreManager: DataStoreManager) {
    fun getTheme(): Flow<String> = dataStoreManager.getTheme()
    suspend fun setTheme(theme: String) = dataStoreManager.setTheme(theme)

    fun getSortOrder(): Flow<String> = dataStoreManager.getSortOrder()
    suspend fun setSortOrder(sortOrder: String) = dataStoreManager.setSortOrder(sortOrder)

    fun getDailyReminder(): Flow<Boolean> = dataStoreManager.getDailyReminder()
    suspend fun setDailyReminder(enabled: Boolean) = dataStoreManager.setDailyReminder(enabled)

    fun getConfirmDelete(): Flow<Boolean> = dataStoreManager.getConfirmDelete()
    suspend fun setConfirmDelete(enabled: Boolean) = dataStoreManager.setConfirmDelete(enabled)
}