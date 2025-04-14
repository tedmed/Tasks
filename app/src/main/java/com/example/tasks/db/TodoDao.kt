package com.example.tasks.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.tasks.data.Todo

@Dao
interface TodoDao {
    @Query("SELECT * FROM Todo where done = false ORDER BY createdAt DESC")
    fun getAllTodos(): LiveData<List<Todo>>

    @Query("SELECT * FROM Todo where done = true ORDER BY createdAt DESC")
    fun getAllDone(): LiveData<List<Todo>>

    @Insert
    fun addTodo(todo: Todo)

    @Update
    fun updateTodo(todo: Todo)

    @Query("DELETE FROM Todo where id = :id")
    fun deleteTodo(id: Int)
}