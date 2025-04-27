package com.example.tasks.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.tasks.R

class TodoReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val todoId = inputData.getInt("todoId", -1)
        val todoTitle = inputData.getString("todoTitle") ?: "Todo Reminder"

        if (todoId == -1) return Result.failure()

        val sharedPrefs = context.getSharedPreferences("user_settings", Context.MODE_PRIVATE)
        val notificationsEnabled = sharedPrefs.getBoolean("daily_reminder", true)

        if (!notificationsEnabled) {
            return Result.success()
        }

        showNotification(todoId, todoTitle)

        return Result.success()
    }

    private fun showNotification(todoId: Int, todoTitle: String) {
        val channelId = "todo_reminder_channel"

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager.getNotificationChannel(channelId) == null) {
            val channel = NotificationChannel(channelId, "Todo Reminders", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val completeIntent = Intent(context, CompleteReceiver::class.java).apply {
            putExtra("todoId", todoId)
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            todoId + 10000,
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(context, SnoozeReceiver::class.java).apply {
            putExtra("todoId", todoId)
            putExtra("todoTitle", todoTitle)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            todoId,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_my_notify_foreground)
            .setContentTitle(todoTitle)
            .setContentText("Don't forget about your task!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(R.drawable.ic_my_notify_foreground, "Complete", completePendingIntent)
            .addAction(R.drawable.ic_my_notify_foreground, "Snooze 5 min", snoozePendingIntent)

        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            NotificationManagerCompat.from(context).notify(todoId, builder.build())
        }
    }
}