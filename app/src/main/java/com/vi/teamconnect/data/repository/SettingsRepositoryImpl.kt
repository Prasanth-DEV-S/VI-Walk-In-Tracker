package com.vi.teamconnect.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.vi.teamconnect.domain.model.AppSettings
import com.vi.teamconnect.domain.repository.SettingsRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : SettingsRepository {

    override fun getAppSettings(): Flow<AppSettings> = callbackFlow {
        val listener = firestore.collection("app_settings")
            .document("dropdown_options")
            .addSnapshotListener { snapshot, _ ->
                val settings = snapshot?.toObject(AppSettings::class.java) ?: AppSettings()
                trySend(settings)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun updateAppSettings(settings: AppSettings): Result<Unit> {
        return try {
            firestore.collection("app_settings")
                .document("dropdown_options")
                .set(settings)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Failed to update dropdown options."))
        }
    }
}
