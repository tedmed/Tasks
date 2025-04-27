package com.example.tasks.workers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

class SnoozeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val todoId = intent.getIntExtra("todoId", -1)
        val todoTitle = intent.getStringExtra("todoTitle") ?: "Todo Reminder"

        if (todoId == -1) return

        NotificationManagerCompat.from(context).cancel(todoId)

        val snoozedWork = OneTimeWorkRequestBuilder<TodoReminderWorker>()
            .setInitialDelay(5, TimeUnit.MINUTES)
            .setInputData(
                workDataOf(
                    "todoId" to todoId,
                    "todoTitle" to todoTitle
                )
            )
            .addTag("todo_reminder_$todoId")
            .build()

        WorkManager.getInstance(context).enqueue(snoozedWork)

        Toast.makeText(context, "Snoozed for 5 minutes!", Toast.LENGTH_SHORT).show()
    }
}