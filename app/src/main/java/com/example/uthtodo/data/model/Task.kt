package com.example.uthtodo.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Data model for a subtask embedded within a Task in Firestore.
 */
data class Subtask(
    val id: String = "",
    val title: String = "",
    @get:PropertyName("isCompleted")
    @set:PropertyName("isCompleted")
    var isCompleted: Boolean = false
)

/**
 * Data model for a Todo task persisted in Firebase Cloud Firestore.
 */
data class Task(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val note: String = "",
    val category: String = "Học tập",
    val priority: String = "Vừa",
    val timeText: String = "",
    val dueDate: Date? = null,
    val hasReminder: Boolean = false,
    @get:PropertyName("isCompleted")
    @set:PropertyName("isCompleted")
    var isCompleted: Boolean = false,
    val subtasks: List<Subtask> = emptyList(),
    @ServerTimestamp
    val createdAt: Date? = null
)
