// AddTodoScreen.kt
package com.example.tasks.screens

import android.icu.text.SymbolTable
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
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.tasks.consts.Routes
import com.example.tasks.viewmodel.TodoViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailScreen(
    todoId: Int,
    viewModel: TodoViewModel,
    navController: NavController
) {
    val todos by viewModel.todoList.collectAsState()
    val dones by viewModel.doneList.collectAsState()
    val todo = (todos + dones).find { it.id == todoId }
    var btnBackClickable by remember { mutableStateOf(true) }
    var btnEditClickable by remember { mutableStateOf(true) }
    val dateFormatter = SimpleDateFormat("d.M.yyyy HH:mm", Locale.ENGLISH)
    if (todo != null) {
        if (!todo.done){
            Scaffold(topBar = {
                TopAppBar(
                    title = { Text("Todo Detail") },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (!btnBackClickable) return@IconButton
                                btnBackClickable = false
                                navController.popBackStack()
                            },
                            enabled = btnBackClickable
                        ) {
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
                    Text("Todo title:\n${todo.title}")
                    Text("Todo description:\n${todo.description}")
                    Text("Todo deadline:\n${dateFormatter.format(todo.deadline)}")
                    Button(
                        onClick = {
                            if (!btnEditClickable) return@Button
                            btnEditClickable = false
                            navController.navigate(Routes.TodoEdit(todo.id))
                        },
                        enabled = btnEditClickable
                    ) {
                        Text("Edit Todo")
                    }
                }
            }
        }
        if (todo.done){
            Scaffold(topBar = {
                TopAppBar(
                    title = { Text("Todo Detail") },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (!btnBackClickable) return@IconButton
                                btnBackClickable = false
                                navController.popBackStack()
                            },
                            enabled = btnBackClickable
                        ) {
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
                    Text("Todo title:\n${todo.title}")
                    Text("Todo description:\n${todo.description}")
                    Text("Todo deadline:\n${dateFormatter.format(todo.deadline)}")
                    Text("This TODO is already done.\nCongratulations \uD83C\uDF89")
                }
            }
        }
    } else {
        Scaffold(topBar = {
            TopAppBar(
                title = { Text("Todo Detail") },
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