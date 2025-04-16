// AddTodoScreen.kt
package com.example.tasks.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
fun TodoEditScreen(
    todoId: Int,
    viewModel: TodoViewModel,
    navController: NavController
) {
    val todos by viewModel.todoList.observeAsState()
    val todo = todos?.find { it.id == todoId }
    var btnBackClickable by remember { mutableStateOf(true) }
    var btnAddClickable by remember { mutableStateOf(true) }
    if (todo != null) {
        var title by remember { mutableStateOf(todo.title) }
        var description by remember { mutableStateOf(todo.description) }
        var deadline by remember { mutableStateOf(todo.deadline) }
        val calendar = Calendar.getInstance().apply {
            time = deadline
        }
        val context = LocalContext.current
        val dateFormatter = SimpleDateFormat("d.M.yyyy HH:mm", Locale.ENGLISH)
        val datePicker = remember {
            android.app.DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val updatedCal = Calendar.getInstance().apply {
                        time = todo.deadline
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    }
                    deadline = updatedCal.time
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }
        val timePicker = remember {
            android.app.TimePickerDialog(
                context,
                { _, hourOfDay, minute ->
                    val updatedCal = Calendar.getInstance().apply {
                        time = deadline
                        set(Calendar.HOUR_OF_DAY, hourOfDay)
                        set(Calendar.MINUTE, minute)
                        set(Calendar.SECOND, 0)
                    }
                    deadline = updatedCal.time
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
        }

        Scaffold(topBar = {
            TopAppBar(
                title = { Text("Edit Todo") },
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
        }) { innerPadding ->
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
                        if (!btnAddClickable) return@Button
                        btnAddClickable = false
                        viewModel.updateTodo(todo.id, title, description, deadline, todo.done)
                        navController.popBackStack()
                    },
                    enabled = btnAddClickable) {
                    Text("Save Changes")
                }
            }
        }
    } else {
        Scaffold(topBar = {
            TopAppBar(
                title = { Text("Edit Todo") },
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
        }) { innerPadding ->
            Text(modifier = Modifier.padding(innerPadding), text = "Todo not found")
        }
    }
}