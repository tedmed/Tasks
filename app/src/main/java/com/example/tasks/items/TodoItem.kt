package com.example.tasks.items

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tasks.R
import com.example.tasks.consts.Routes
import com.example.tasks.data.Todo
import com.example.tasks.helpers.DeleteTodoConfirmationDialog
import com.example.tasks.viewmodel.SettingsViewModel
import com.example.tasks.viewmodel.TodoViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TodoItem(todo: Todo, viewModel: TodoViewModel, navController: NavController, settingsViewModel: SettingsViewModel = koinViewModel()) {
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val confirmDelete by settingsViewModel.confirmDelete.collectAsState()

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
        .clip(RoundedCornerShape(16.dp))
        .background(MaterialTheme.colorScheme.primary)
        .padding(8.dp)
        .clickable { navController.navigate(Routes.TodoDetail(todo.id)) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(modifier = Modifier.padding(8.dp), checked = todo.done, onCheckedChange = {
            viewModel.updateTodo(todo.id, todo.title, todo.description, todo.deadline, !todo.done)
        })
        Column(modifier = Modifier.weight(1f)) {
            Text(text = todo.title, fontSize = 20.sp, color = Color.White)
            Text(text = SimpleDateFormat("d.M.yyyy HH:mm", Locale.ENGLISH).format(todo.deadline), fontSize = 12.sp, color = Color.White)
        }
        IconButton(onClick = {
            if (confirmDelete) {
                showDeleteConfirmDialog = true
            } else {
                viewModel.deleteTodo(todo.id)
                Toast.makeText(context, "Todo deleted", Toast.LENGTH_SHORT).show()
            }
        }) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_delete_forever_24),
                contentDescription = "Delete"
            )
        }
    }
//
//    val swipeState = rememberSwipeToDismissBoxState(
//        positionalThreshold = { it }
//    )
//
//    SwipeToDismissBox(
//        state = swipeState,
//        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
//        backgroundContent = {
//            val direction = swipeState.dismissDirection
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .clip(RoundedCornerShape(16.dp))
//                    .background(
//                        when (direction) {
//                            SwipeToDismissBoxValue.StartToEnd -> Color(0xFF4CAF50)
//                            SwipeToDismissBoxValue.EndToStart -> Color(0xFFF44336)
//                            else -> Color.Transparent
//                        }
//                    ),
//                contentAlignment = when (direction) {
//                    SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
//                    SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
//                    else -> Alignment.Center
//                }
//            ) {
//                when (direction) {
//                    SwipeToDismissBoxValue.StartToEnd -> {
//                        Checkbox(
//                            checked = todo.done,
//                            onCheckedChange = {
//                                viewModel.updateTodo(
//                                    id = todo.id,
//                                    title = todo.title,
//                                    description = todo.description,
//                                    createdAt = todo.createdAt,
//                                    done = !todo.done
//                                )
//                                coroutineScope.launch { swipeState.reset() }
//                            },
//                            modifier = Modifier.padding(start = 16.dp)
//                        )
//                    }
//
//                    SwipeToDismissBoxValue.EndToStart -> {
//                        IconButton(
//                            onClick = {
//                                if (confirmDelete) {
//                                    showDeleteConfirmDialog = true
//                                } else {
//                                    viewModel.deleteTodo(todo.id)
//                                    Toast.makeText(context, "Todo deleted", Toast.LENGTH_SHORT).show()
//                                }
//                                coroutineScope.launch { swipeState.reset() }
//                            },
//                            modifier = Modifier.padding(end = 16.dp)
//                        ) {
//                            Icon(
//                                painter = painterResource(id = R.drawable.baseline_delete_forever_24),
//                                contentDescription = "Delete",
//                                tint = Color.White
//                            )
//                        }
//                    }
//
//                    else -> {}
//                }
//            }
//        }
//    ) {
//        Surface(
//            modifier = Modifier
//                .fillMaxWidth()
//                .clip(RoundedCornerShape(16.dp)),
//            color = MaterialTheme.colorScheme.primary
//        ) {
//            Column(modifier = Modifier.padding(16.dp)) {
//                Text(text = todo.title, fontSize = 20.sp, color = Color.White)
//                Text(
//                    text = SimpleDateFormat("M.dd.yyyy HH:mm:ss", Locale.ENGLISH).format(todo.createdAt),
//                    fontSize = 12.sp,
//                    color = Color.White
//                )
//            }
//        }
//    }

    if (showDeleteConfirmDialog) {
        DeleteTodoConfirmationDialog(
            todoTitle = todo.title,
            onConfirmDelete = {
                viewModel.deleteTodo(todo.id)
                showDeleteConfirmDialog = false
            },
            onDismiss = {
                showDeleteConfirmDialog = false
                Toast.makeText(context, "User canceled the deletion", Toast.LENGTH_SHORT).show()
            }
        )
    }
}