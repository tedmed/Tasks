package com.example.tasks.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tasks.data.Todo
import com.example.tasks.items.TodoItem
import com.example.tasks.viewmodel.SettingsViewModel
import com.example.tasks.viewmodel.TodoViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TodoListScreen(viewModel: TodoViewModel, settingsViewModel: SettingsViewModel = koinViewModel()){
    val todoList by viewModel.todoList.observeAsState()
    val sortOrder by settingsViewModel.sortOrder.collectAsState()
    var inputText by remember {
        mutableStateOf("")
    }

    val sortedList = remember(todoList, sortOrder) {
        todoList?.let {
            when (sortOrder) {
                "oldest" -> it.sortedBy { it.createdAt }
                "alphabet" -> it.sortedBy { it.title.lowercase() }
                else -> it.sortedByDescending { it.createdAt }
            }
        }
    }

    Column(modifier = Modifier.fillMaxHeight()
        .padding(8.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()
            .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly) {
            OutlinedTextField(value = inputText, onValueChange = {inputText=it})
            if (inputText.isNotBlank()){
                Button(onClick = {
                    viewModel.addTodo(title = inputText, description = "No desc")
                    inputText = ""}) {
                    Text("Add")
                }
            }

        }
        sortedList?.let {
            LazyColumn(content = {
                itemsIndexed(it){
                        index: Int, item: Todo ->
                    TodoItem(todo = item, viewModel = viewModel, settingsViewModel = settingsViewModel)
                }
            })
        }?: Text(text = "No todos yet")
    }
}