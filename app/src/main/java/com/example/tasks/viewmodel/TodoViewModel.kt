package com.example.tasks.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasks.data.Todo
import com.example.tasks.db.TodoDao
import com.example.tasks.repository.SettingsRepository
import com.example.tasks.workers.TodoReminderScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

class TodoViewModel(
    private val todoDao: TodoDao,
    settingsRepository: SettingsRepository
): ViewModel() {
    val todoList: StateFlow<List<Todo>> = todoDao.getAllTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val doneList: StateFlow<List<Todo>> = todoDao.getAllDone()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sortedTodoList: StateFlow<List<Todo>> = combine(
        todoDao.getAllTodos(),
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
        todoDao.getAllDone(),
        settingsRepository.getSortOrder()
    ) { dones, sortOrder ->
        when (sortOrder) {
            "exp. latest" -> dones.sortedByDescending { it.deadline }
            "alphabet" -> dones.sortedBy { it.title.lowercase() }
            else -> dones.sortedBy { it.deadline }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTodo(context: Context, title: String, description: String, deadline: Date, reminderMinutesBefore: Int?){
        viewModelScope.launch(Dispatchers.IO) {
            val todo = Todo(title = title, description = description, deadline = deadline, done = false, reminderMinutesBefore = reminderMinutesBefore)
            val newId = todoDao.addTodo(todo).toInt()
            reminderMinutesBefore?.let {
                val reminderTime = Date(deadline.time - it * 60 * 1000)
                TodoReminderScheduler.scheduleTodoReminder(context, newId, title, reminderTime)
            }
        }
    }

    fun deleteTodo(context: Context, id: Int){
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.deleteTodo(id)
            TodoReminderScheduler.cancelTodoReminder(context, id)
        }
    }

    fun updateTodo(context: Context, id: Int, title: String, description: String, deadline: Date, done: Boolean, reminderMinutesBefore: Int?){
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.updateTodo(Todo(id = id, title = title, description = description, deadline = deadline, done = done, reminderMinutesBefore = reminderMinutesBefore))
            TodoReminderScheduler.cancelTodoReminder(context, id)
            reminderMinutesBefore?.let {
                val reminderTime = Date(deadline.time - it * 60 * 1000)
                TodoReminderScheduler.scheduleTodoReminder(context, id, title, reminderTime)
            }
        }
    }

    fun markTodoDone(context: Context, todo: Todo, done: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            val updatedTodo = todo.copy(done = done)
            todoDao.updateTodo(updatedTodo)
            if (done) {
                TodoReminderScheduler.cancelTodoReminder(context, todo.id)
            } else {
                updatedTodo.reminderMinutesBefore?.let { minutesBefore ->
                    val reminderTime = Date(updatedTodo.deadline.time - minutesBefore * 60 * 1000)
                    TodoReminderScheduler.scheduleTodoReminder(context, updatedTodo.id, updatedTodo.title, reminderTime)
                }
            }
        }
    }
}