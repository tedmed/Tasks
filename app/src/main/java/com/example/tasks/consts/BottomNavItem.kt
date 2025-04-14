package com.example.tasks.consts

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val title: String, val icon: ImageVector, val screenRoute: String) {
    object TodoList : BottomNavItem("Todo", Icons.Filled.Home, Routes.TodoList)
    object DoneList : BottomNavItem("Done", Icons.Filled.Done, Routes.DoneList)
    object Settings : BottomNavItem("Settings", Icons.Filled.Settings, Routes.Settings)
}