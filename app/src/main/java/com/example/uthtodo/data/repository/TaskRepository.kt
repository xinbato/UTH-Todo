package com.example.uthtodo.data.repository

import com.example.uthtodo.data.model.Subtask
import com.example.uthtodo.data.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Interface contract for Task data operations.
 */
interface TaskRepository {
    fun getTasksFlow(userId: String): Flow<List<Task>>
    suspend fun addTask(task: Task): Result<String>
    suspend fun updateTaskStatus(taskId: String, isCompleted: Boolean): Result<Unit>
    suspend fun deleteTask(taskId: String): Result<Unit>
    suspend fun updateSubtasks(taskId: String, subtasks: List<Subtask>): Result<Unit>
}

/**
 * Concrete implementation of TaskRepository backed by Firebase Cloud Firestore.
 */
class FirestoreTaskRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : TaskRepository {

    private val tasksCollection = firestore.collection("tasks")

    override fun getTasksFlow(userId: String): Flow<List<Task>> = callbackFlow {
        val subscription = tasksCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val tasks = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Task::class.java)
                }?.sortedByDescending { it.createdAt } ?: emptyList()
                trySend(tasks)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun addTask(task: Task): Result<String> = runCatching {
        val docRef = tasksCollection.add(task).await()
        docRef.id
    }

    override suspend fun updateTaskStatus(taskId: String, isCompleted: Boolean): Result<Unit> = runCatching {
        tasksCollection.document(taskId).update("isCompleted", isCompleted).await()
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> = runCatching {
        tasksCollection.document(taskId).delete().await()
    }

    override suspend fun updateSubtasks(taskId: String, subtasks: List<Subtask>): Result<Unit> = runCatching {
        tasksCollection.document(taskId).update("subtasks", subtasks).await()
    }
}
