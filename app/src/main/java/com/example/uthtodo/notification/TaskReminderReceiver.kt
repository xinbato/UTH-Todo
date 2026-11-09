package com.example.uthtodo.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * BroadcastReceiver triggered by AlarmManager when a task reminder is due.
 */
class TaskReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getStringExtra(NotificationHelper.EXTRA_TASK_ID)
        val title = intent.getStringExtra(NotificationHelper.EXTRA_TITLE) ?: "Nhắc nhở công việc"
        val message = intent.getStringExtra(NotificationHelper.EXTRA_MESSAGE) ?: "Đã đến hạn thực hiện công việc!"

        NotificationHelper.showNotification(
            context = context,
            title = title,
            message = message,
            taskId = taskId
        )
    }
}
