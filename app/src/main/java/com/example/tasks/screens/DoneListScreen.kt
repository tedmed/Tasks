package com.example.tasks.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tasks.data.Todo
import com.example.tasks.items.TodoItem
import com.example.tasks.viewmodel.SettingsViewModel
import com.example.tasks.viewmodel.TodoViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DoneListScreen(viewModel: TodoViewModel, navController: NavController, settingsViewModel: SettingsViewModel = koinViewModel()){
    val sortedList by viewModel.sortedDoneList.collectAsState()

    Column(modifier = Modifier.fillMaxHeight()
        .padding(8.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        sortedList.let {
            LazyColumn(content = {
                itemsIndexed(it){
                        index: Int, item: Todo ->
                    TodoItem(todo = item, viewModel = viewModel, navController = navController, settingsViewModel = settingsViewModel)
                }
            })
        }
        if(sortedList.isEmpty()) Text(text = "No todos yet")
    }
}