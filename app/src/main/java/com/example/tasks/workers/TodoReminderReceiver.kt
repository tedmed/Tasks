package com.example.tasks.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.room.Room
import com.example.tasks.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class TodoReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val todoId = intent.getIntExtra("todoId", -1)
        val todoTitle = intent.getStringExtra("todoTitle") ?: "Todo Reminder"
        val action = intent.getStringExtra("action") ?: "reminder"
        if (todoId == -1) return

        val sharedPrefs = context.getSharedPreferences("user_settings", Context.MODE_PRIVATE)
        val todoNotificationsEnabled = sharedPrefs.getBoolean("daily_reminder", true)

        if (!todoNotificationsEnabled) {
            // If user turned off notifications, do nothing
            return
        }

        val channelId = "todo_reminder_channel"
        val notificationId = todoId // Use todoId for unique notifications

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager.getNotificationChannel(channelId) == null) {
            val name = "Todo Reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance)
            notificationManager.createNotificationChannel(channel)
        }

        if (action == "complete") {
            // User clicked Complete
            NotificationManagerCompat.from(context).cancel(todoId)

            // Update the Todo as done
            markTodoAsDone(context, todoId)

            return
        }

        if (action == "snooze") {
            // User clicked Snooze -> schedule again 5 minutes later + cancelled current notification
            NotificationManagerCompat.from(context).cancel(todoId)
            Toast.makeText(context, "Reminder snoozed for 5 minutes", Toast.LENGTH_SHORT).show()
            val reminderTime = Date(System.currentTimeMillis() + 5 * 60 * 1000) // 5 minutes later
            TodoReminderScheduler.scheduleTodoReminder(context, todoId, todoTitle, reminderTime )
        } else {
            // Normal Reminder -> show Notification with Snooze Button
            val completeIntent = Intent(context, TodoReminderReceiver::class.java).apply {
                putExtra("todoId", todoId)
                putExtra("todoTitle", todoTitle)
                putExtra("action", "complete")
            }
            val completePendingIntent = PendingIntent.getBroadcast(
                context,
                todoId + 20000, // Different requestCode to avoid collision
                completeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val snoozeIntent = Intent(context, TodoReminderReceiver::class.java).apply {
                putExtra("todoId", todoId)
                putExtra("todoTitle", todoTitle)
                putExtra("action", "snooze")
            }
            val snoozePendingIntent = PendingIntent.getBroadcast(
                context,
                todoId + 10000, // Different requestCode to avoid collision
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
                NotificationManagerCompat.from(context).notify(notificationId, builder.build())
            }
        }
    }

    private fun markTodoAsDone(context: Context, todoId: Int) {
        val db = Room.databaseBuilder(
            context,
            com.example.tasks.db.TodoDatabase::class.java,
            com.example.tasks.db.TodoDatabase.NAME
        ).build()
        val todoDao = db.getTodoDao()

        CoroutineScope(Dispatchers.IO).launch {
            val todo = todoDao.getTodoById(todoId) // You'll need to add this function!
            if (todo != null) {
                todoDao.updateTodo(todo.copy(done = true))
            }
        }
    }
}