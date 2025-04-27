package com.example.tasks.workers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import com.example.tasks.db.TodoDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val todoId = intent.getIntExtra("todoId", -1)
        if (todoId == -1) return

        CoroutineScope(Dispatchers.IO).launch {
            val db = TodoDatabase.getInstance(context)
            val todoDao = db.getTodoDao()
            val todo = todoDao.getTodoById(todoId)
            if (todo != null) {
                todoDao.updateTodo(todo.copy(done = true))

                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "Todo marked as complete!", Toast.LENGTH_SHORT).show()
                }

                NotificationManagerCompat.from(context).cancel(todoId)
            }
        }
    }
}