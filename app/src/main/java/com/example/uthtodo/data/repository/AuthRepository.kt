package com.example.uthtodo.data.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * OOP Data class representing authenticated user data.
 */
data class AuthUser(
    val uid: String,
    val displayName: String?,
    val email: String?,
    val photoUrl: String?,
    val isAnonymous: Boolean = false
)

/**
 * Extension property to format user display name initials consistently.
 */
val AuthUser?.initials: String
    get() {
        if (this == null || this.isAnonymous) return "UTH"
        val name = this.displayName
        if (name.isNullOrBlank()) return "UTH"
        return name.split(" ")
            .mapNotNull { it.firstOrNull()?.toString() }
            .takeLast(2)
            .joinToString("")
            .uppercase()
            .ifEmpty { name.take(2).uppercase() }
    }

/**
 * OOP Interface contract for Authentication Repository.
 */
interface AuthRepository {
    val currentUserFlow: Flow<AuthUser?>
    fun getCurrentUser(): AuthUser?
    suspend fun signInWithGoogle(context: Context): Result<AuthUser>
    suspend fun signInAnonymously(): Result<AuthUser>
    fun signOut()
}

/**
 * Concrete OOP Implementation using Firebase Authentication & Android Credential Manager API.
 */
class FirebaseAuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {

    override val currentUserFlow: Flow<AuthUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            trySend(user?.let {
                AuthUser(
                    uid = it.uid,
                    displayName = it.displayName,
                    email = it.email,
                    photoUrl = it.photoUrl?.toString(),
                    isAnonymous = it.isAnonymous
                )
            })
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override fun getCurrentUser(): AuthUser? {
        val user = firebaseAuth.currentUser ?: return null
        return AuthUser(
            uid = user.uid,
            displayName = user.displayName,
            email = user.email,
            photoUrl = user.photoUrl?.toString(),
            isAnonymous = user.isAnonymous
        )
    }

    override suspend fun signInAnonymously(): Result<AuthUser> {
        return runCatching {
            val authResult = firebaseAuth.signInAnonymously().await()
            val user = authResult.user ?: throw IllegalStateException("Không thể tạo tài khoản ẩn danh từ Firebase")
            AuthUser(
                uid = user.uid,
                displayName = user.displayName,
                email = user.email,
                photoUrl = user.photoUrl?.toString(),
                isAnonymous = user.isAnonymous
            )
        }
    }

    override suspend fun signInWithGoogle(context: Context): Result<AuthUser> {
        return runCatching {
            val webClientId = try {
                context.getString(com.example.uthtodo.R.string.default_web_client_id)
            } catch (_: Exception) {
                ""
            }

            if (webClientId.isEmpty()) {
                throw IllegalStateException("Vui lòng cấu hình google-services.json chứa Web Client ID trong thư mục app/")
            }

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credentialManager = CredentialManager.create(context)
            val result = try {
                credentialManager.getCredential(context = context, request = request)
            } catch (_: NoCredentialException) {
                throw IllegalStateException("Không tìm thấy thông tin xác thực Google. Đảm bảo thiết bị đã có tài khoản Google và SHA-1 của keystore Release đã được thêm trên Firebase Console.")
            } catch (_: GetCredentialCancellationException) {
                throw IllegalStateException("Bạn đã hủy thao tác đăng nhập.")
            } catch (e: GetCredentialException) {
                throw IllegalStateException("Lỗi Credential Manager: ${e.localizedMessage}")
            }

            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken
                val authCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = firebaseAuth.signInWithCredential(authCredential).await()

                val user = authResult.user ?: throw IllegalStateException("Không thể lấy thông tin người dùng từ Firebase")
                AuthUser(
                    uid = user.uid,
                    displayName = user.displayName,
                    email = user.email,
                    photoUrl = user.photoUrl?.toString()
                )
            } else {
                throw IllegalStateException("Loại thông tin xác thực không hợp lệ")
            }
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}
