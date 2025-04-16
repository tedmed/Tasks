package com.example.tasks.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

private val Context.dataStore by preferencesDataStore(name = "user_settings")

class DataStoreManager(private val context: Context) {

    companion object {
        val THEME = stringPreferencesKey("theme") // "light", "dark", "system"
        val SORT_ORDER = stringPreferencesKey("sort_order") // "exp. soon", "exp. latest", "alphabet"
        val SHOW_EXPIRED = booleanPreferencesKey("show_expired")
        val DAILY_REMINDER = booleanPreferencesKey("daily_reminder")
        val CONFIRM_DELETE = booleanPreferencesKey("confirm_delete")
    }

    fun getTheme(): Flow<String> = context.dataStore.data.map { it[THEME] ?: "system" }
    suspend fun setTheme(value: String) = context.dataStore.edit { it[THEME] = value }

    fun getSortOrder(): Flow<String> = context.dataStore.data.map { it[SORT_ORDER] ?: "exp. soon" }
    suspend fun setSortOrder(value: String) = context.dataStore.edit { it[SORT_ORDER] = value }

    fun getShowExpired(): Flow<Boolean> = context.dataStore.data.map { it[SHOW_EXPIRED] ?: true }
    suspend fun setShowExpired(value: Boolean) = context.dataStore.edit { it[SHOW_EXPIRED] = value }

    fun getDailyReminder(): Flow<Boolean> = context.dataStore.data.map { it[DAILY_REMINDER] ?: false }
    suspend fun setDailyReminder(enabled: Boolean) = context.dataStore.edit { it[DAILY_REMINDER] = enabled }

    fun getConfirmDelete(): Flow<Boolean> = context.dataStore.data.map { it[CONFIRM_DELETE] ?: true }
    suspend fun setConfirmDelete(enabled: Boolean) = context.dataStore.edit { it[CONFIRM_DELETE] = enabled }
}