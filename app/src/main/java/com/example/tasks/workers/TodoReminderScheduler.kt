package com.example.tasks.workers

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.Date
import java.util.concurrent.TimeUnit

object TodoReminderScheduler {
    fun scheduleTodoReminder(context: Context, todoId: Int, todoTitle: String, reminderTime: Date) {
        val delayMillis = reminderTime.time - System.currentTimeMillis()
        if (delayMillis <= 0) return

        val workRequest = OneTimeWorkRequestBuilder<TodoReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    "todoId" to todoId,
                    "todoTitle" to todoTitle
                )
            )
            .addTag("todo_reminder_$todoId")
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    fun cancelTodoReminder(context: Context, todoId: Int) {
        WorkManager.getInstance(context).cancelAllWorkByTag("todo_reminder_$todoId")
    }
}