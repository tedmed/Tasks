package com.example.tasks.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasks.MainApplication
import com.example.tasks.data.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date

class TodoViewModel: ViewModel() {
    val todoDao = MainApplication.todoDatabase.getTodoDao()
    val todoList: LiveData<List<Todo>> = todoDao.getAllTodos()
    val doneList: LiveData<List<Todo>> = todoDao.getAllDone()

    fun addTodo(title: String, description: String){
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.addTodo(Todo(title = title, description = description, createdAt = Date.from(Instant.now()), done = false))
        }
    }

    fun deleteTodo(id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.deleteTodo(id)
        }
    }

    fun updateTodo(id: Int, title: String, description: String, createdAt: Date, done: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.updateTodo(Todo(id = id, title = title, description = description, createdAt = createdAt, done = done))
        }
    }
}