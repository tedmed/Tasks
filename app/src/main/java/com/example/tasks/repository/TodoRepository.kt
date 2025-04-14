package com.example.tasks.repository

import com.example.tasks.data.Todo
import java.time.Instant
import java.util.Date

class TodoRepository {
    private val todoList = mutableListOf<Todo>()

    fun getAllTodos(): List<Todo>{
        return todoList
    }

    fun addTodo(title: String, description: String){
        todoList.add(Todo(System.currentTimeMillis().toInt(), title, description, Date.from(Instant.now()), false))
    }

    fun deleteTodo(id: Int){
        todoList.removeIf{
            it.id == id
        }
    }
}