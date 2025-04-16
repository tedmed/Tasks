package com.example.tasks.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val deadline: Date,
    val done: Boolean
)