package com.example.uthtodo.data.repository

import com.example.uthtodo.data.model.AppNotification
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Interface contract for Notification operations.
 */
interface NotificationRepository {
    fun getNotificationsFlow(userId: String): Flow<List<AppNotification>>
    suspend fun addNotification(notification: AppNotification): Result<String>
    suspend fun markAsRead(notificationId: String): Result<Unit>
    suspend fun markAllAsRead(userId: String): Result<Unit>
    suspend fun deleteNotification(notificationId: String): Result<Unit>
}

/**
 * Concrete implementation backed by Firebase Cloud Firestore collection "notifications".
 */
class FirestoreNotificationRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : NotificationRepository {

    private val notificationsCollection = firestore.collection("notifications")

    override fun getNotificationsFlow(userId: String): Flow<List<AppNotification>> = callbackFlow {
        val subscription = notificationsCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(AppNotification::class.java)
                }?.sortedByDescending { it.createdAt } ?: emptyList()
                trySend(items)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun addNotification(notification: AppNotification): Result<String> = runCatching {
        val docRef = notificationsCollection.add(notification).await()
        docRef.id
    }

    override suspend fun markAsRead(notificationId: String): Result<Unit> = runCatching {
        notificationsCollection.document(notificationId).update("isRead", true).await()
    }

    override suspend fun markAllAsRead(userId: String): Result<Unit> = runCatching {
        val snapshot = notificationsCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("isRead", false)
            .get()
            .await()

        val batch = firestore.batch()
        for (doc in snapshot.documents) {
            batch.update(doc.reference, "isRead", true)
        }
        batch.commit().await()
    }

    override suspend fun deleteNotification(notificationId: String): Result<Unit> = runCatching {
        notificationsCollection.document(notificationId).delete().await()
    }
}
