// AddTodoScreen.kt
package com.example.tasks.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tasks.viewmodel.TodoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailScreen(
    todoId: Int,
    viewModel: TodoViewModel,
    navController: NavController
) {
    val todos by viewModel.todoList.observeAsState()
    val todo = todos?.find { it.id == todoId }

    var title by remember { mutableStateOf(todo?.title ?: "") }
    var description by remember { mutableStateOf(todo?.description ?: "") }
    var btnClickable by remember { mutableStateOf(true) }

    if (todo != null) {
        Scaffold(topBar = {
            TopAppBar(
                title = { Text("Edit Todo") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (!btnClickable) return@IconButton
                            btnClickable = false
                            navController.popBackStack() },
                        enabled = btnClickable) {
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

                Button(
                    onClick = {
                        if (!btnClickable) return@Button
                        btnClickable = false
                        viewModel.updateTodo(todo.id, title, description, todo.createdAt, todo.done)
                        navController.popBackStack()
                    },
                    enabled = btnClickable) {
                    Text("Save Changes")
                }
            }
        }
    } else {
        Text("Todo not found")
    }
}