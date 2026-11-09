package com.example.uthtodo.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Data model for in-app and push notification records stored in Firebase Cloud Firestore.
 */
data class AppNotification(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val taskId: String? = null,
    val title: String = "",
    val message: String = "",
    val type: String = "REMINDER", // REMINDER, OVERDUE, SYSTEM, DAILY_SUMMARY
    val isRead: Boolean = false,
    @ServerTimestamp
    val createdAt: Date? = null
)
