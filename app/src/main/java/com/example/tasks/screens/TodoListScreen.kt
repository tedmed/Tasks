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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tasks.R
import com.example.tasks.consts.Routes
import com.example.tasks.data.Todo
import com.example.tasks.items.TodoItem
import com.example.tasks.viewmodel.SettingsViewModel
import com.example.tasks.viewmodel.TodoViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun TodoListScreen(viewModel: TodoViewModel, navController: NavController, settingsViewModel: SettingsViewModel = koinViewModel()){
    var btnClickable by remember { mutableStateOf(true) }
    val sortedList by viewModel.sortedTodoList.collectAsState()

    Column(modifier = Modifier.fillMaxHeight()
        .padding(8.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(
                onClick = {
                    if (!btnClickable) return@Button
                    btnClickable = false
                    navController.navigate(Routes.AddTodo)
                },
                enabled = btnClickable) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = "Add a TODO"
                )
            }
        }
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