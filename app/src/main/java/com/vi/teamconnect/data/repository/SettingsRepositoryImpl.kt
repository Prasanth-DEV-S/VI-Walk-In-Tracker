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
            .document("globle")
            .addSnapshotListener { globleSnapshot, _ ->
                val globleBranches = (globleSnapshot?.get("branchName") as? List<*>)
                    ?.mapNotNull { it as? String }

                firestore.collection("app_settings")
                    .document("dropdown_options")
                    .get()
                    .addOnSuccessListener { dropdownSnapshot ->
                        val baseSettings = dropdownSnapshot?.toObject(AppSettings::class.java) ?: AppSettings()
                        val finalBranches = if (!globleBranches.isNullOrEmpty()) globleBranches else baseSettings.branchName
                        trySend(baseSettings.copy(branchName = finalBranches))
                    }
                    .addOnFailureListener {
                        val baseSettings = AppSettings()
                        val finalBranches = if (!globleBranches.isNullOrEmpty()) globleBranches else baseSettings.branchName
                        trySend(baseSettings.copy(branchName = finalBranches))
                    }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun updateAppSettings(settings: AppSettings): Result<Unit> {
        return try {
            firestore.collection("app_settings")
                .document("globle")
                .set(mapOf("branchName" to settings.branchName))
                .await()

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
