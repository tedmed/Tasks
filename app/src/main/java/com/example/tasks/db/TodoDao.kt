package com.example.tasks.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.tasks.data.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM Todo where done = false")
    fun getAllTodos(): Flow<List<Todo>>

    @Query("SELECT * FROM Todo where done = true")
    fun getAllDone(): Flow<List<Todo>>

    @Insert
    suspend fun addTodo(todo: Todo): Long

    @Update
    suspend fun updateTodo(todo: Todo)

    @Query("DELETE FROM Todo where id = :id")
    suspend fun deleteTodo(id: Int)

    @Query("SELECT * FROM Todo WHERE id = :id")
    suspend fun getTodoById(id: Int): Todo?
}