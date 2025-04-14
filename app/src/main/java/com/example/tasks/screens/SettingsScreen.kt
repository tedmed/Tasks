package com.example.tasks.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tasks.viewmodel.SettingsViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel = koinViewModel()){
    val theme by settingsViewModel.theme.collectAsState()
    val sortOrder by settingsViewModel.sortOrder.collectAsState()
    val dailyReminder by settingsViewModel.dailyReminderEnabled.collectAsState()
    val confirmDelete by settingsViewModel.confirmDelete.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)

        // Theme Setting
        Text("Theme")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("light", "dark", "system").forEach {
                FilterChip(
                    selected = theme == it,
                    onClick = { settingsViewModel.updateTheme(it) },
                    label = { Text(it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }) }
                )
            }
        }

        // Sort Order Setting
        Text("Default Sort Order")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("newest", "oldest", "alphabet").forEach {
                FilterChip(
                    selected = sortOrder == it,
                    onClick = { settingsViewModel.updateSortOrder(it) },
                    label = { Text(it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }) }
                )
            }
        }

        // Daily Reminder Toggle
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Daily Reminder at 9:00", modifier = Modifier.weight(1f))
            Switch(
                checked = dailyReminder,
                onCheckedChange = { settingsViewModel.updateDailyReminder(it, context) }
            )
        }

        // Confirm Delete Toggle
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Confirm Before Deleting Todos", modifier = Modifier.weight(1f))
            Switch(
                checked = confirmDelete,
                onCheckedChange = { settingsViewModel.updateConfirmDelete(it) }
            )
        }
    }
}