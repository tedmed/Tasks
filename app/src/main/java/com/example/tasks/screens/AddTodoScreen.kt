// AddTodoScreen.kt
package com.example.tasks.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tasks.viewmodel.TodoViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoScreen(
    viewModel: TodoViewModel,
    navController: NavController
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var btnBackClickable by remember { mutableStateOf(true) }
    var btnAddClickable by remember { mutableStateOf(true) }
    var deadline by remember { mutableStateOf(Date()) }
    val calendar = Calendar.getInstance().apply {
        time = deadline
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val context = LocalContext.current
    val dateFormatter = SimpleDateFormat("d.M.yyyy HH:mm", Locale.ENGLISH)
    val datePicker = remember {
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                deadline = calendar.time
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }
    val timePicker = remember {
        android.app.TimePickerDialog(
            context,
            { _, selectedHour, selectedMinute ->
                calendar.time = deadline
                calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                calendar.set(Calendar.MINUTE, selectedMinute)
                calendar.set(Calendar.SECOND, 0)
                deadline = calendar.time
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Todo") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (!btnBackClickable) return@IconButton
                            btnBackClickable = false
                            navController.popBackStack() },
                        enabled = btnBackClickable) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
            Text("Deadline: ${dateFormatter.format(deadline)}")
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = { datePicker.show() }) {
                    Text("Pick Date")
                }
                Button(onClick = { timePicker.show() }) {
                    Text("Pick Time")
                }
            }
            Button(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank()) {
                        if (!btnAddClickable) return@Button
                        btnAddClickable = false
                        viewModel.addTodo(title, description, deadline)
                        navController.popBackStack()
                    }
                },
                enabled = btnAddClickable) {
                Text("Add Todo")
            }
        }
    }
}
