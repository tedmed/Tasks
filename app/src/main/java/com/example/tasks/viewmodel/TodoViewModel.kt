package com.example.tasks.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.tasks.data.Todo
import com.example.tasks.db.TodoDao
import com.example.tasks.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.Date

class TodoViewModel(
    private val todoDao: TodoDao,
    private val settingsRepository: SettingsRepository
): ViewModel() {
    val todoList: LiveData<List<Todo>> = todoDao.getAllTodos()
    val doneList: LiveData<List<Todo>> = todoDao.getAllDone()

    val sortedTodoList: StateFlow<List<Todo>> = combine(
        todoDao.getAllTodos().asFlow(),
        settingsRepository.getSortOrder(),
        settingsRepository.getShowExpired()
    ) { todos, sortOrder, showExpired ->
        val filtered = if (showExpired) todos else todos.filter { it.deadline.after(Date()) }
        when (sortOrder) {
            "exp. latest" -> filtered.sortedByDescending { it.deadline }
            "alphabet" -> filtered.sortedBy { it.title.lowercase() }
            else -> filtered.sortedBy { it.deadline }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sortedDoneList: StateFlow<List<Todo>> = combine(
        todoDao.getAllDone().asFlow(),
        settingsRepository.getSortOrder()
    ) { dones, sortOrder ->
        when (sortOrder) {
            "exp. latest" -> dones.sortedByDescending { it.deadline }
            "alphabet" -> dones.sortedBy { it.title.lowercase() }
            else -> dones.sortedBy { it.deadline }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTodo(title: String, description: String, deadline: Date){
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.addTodo(Todo(title = title, description = description, deadline = deadline, done = false))
        }
    }

    fun deleteTodo(id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.deleteTodo(id)
        }
    }

    fun updateTodo(id: Int, title: String, description: String, deadline: Date, done: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.updateTodo(Todo(id = id, title = title, description = description, deadline = deadline, done = done))
        }
    }

    fun markTodoDone(todo: Todo, done: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.updateTodo(todo.copy(done = done))
        }
    }
}