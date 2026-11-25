package com.vi.teamconnect.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.vi.teamconnect.data.mapper.toDomain
import com.vi.teamconnect.data.mapper.toDto
import com.vi.teamconnect.data.model.BranchDto
import com.vi.teamconnect.data.model.StoreDto
import com.vi.teamconnect.domain.model.Branch
import com.vi.teamconnect.domain.model.Store
import com.vi.teamconnect.domain.repository.BranchRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BranchRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BranchRepository {

    private val defaultBranchNames = listOf(
        "AS TRADERS",
        "GLOBAL COMMUNICATION",
        "HITECH CONSULTANCY",
        "SAS ENTERPRISES",
        "KK NAGAR",
        "SIA Hubify",
        "SHREE CHAKRA AGENCY",
        "SKM COMMUNICATION",
        "SMS COMMUNICATION",
        "SRI SAIRAM AENCY",
        "VENKAT SAI AGENCIES",
        "ATB Agency",
        "P&C",
        "Kalai Communication",
        "Rajas"
    )

    private val staticBranches = defaultBranchNames.mapIndexed { idx, name ->
        Branch(
            branchId = "BRANCH-${(idx + 1).toString().padStart(2, '0')}",
            branchName = name,
            branchCode = name.split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("").take(4).ifEmpty { "BR${idx + 1}" }
        )
    }

    override fun getAllBranches(): Flow<List<Branch>> = callbackFlow {
        val listener = firestore.collection("app_settings")
            .document("globle")
            .addSnapshotListener { globleSnapshot, _ ->
                val globleBranchStrings = (globleSnapshot?.get("branchName") as? List<*>)
                    ?.mapNotNull { it as? String }
                    ?: emptyList()

                val globleBranches = globleBranchStrings.mapIndexed { idx, name ->
                    Branch(
                        branchId = "BRANCH-${(idx + 1).toString().padStart(2, '0')}",
                        branchName = name,
                        branchCode = name.split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("").take(4).ifEmpty { "BR${idx + 1}" }
                    )
                }

                firestore.collection("branches")
                    .whereEqualTo("isActive", true)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val firestoreList = snapshot?.documents?.mapNotNull { doc ->
                            try {
                                doc.toObject(BranchDto::class.java)?.toDomain()
                            } catch (_: Exception) {
                                null
                            }
                        } ?: emptyList()

                        val combined = (firestoreList + globleBranches + staticBranches).distinctBy { it.branchName }
                        trySend(combined)
                    }
                    .addOnFailureListener {
                        val combined = (globleBranches + staticBranches).distinctBy { it.branchName }
                        trySend(combined)
                    }
            }
        awaitClose { listener.remove() }
    }

    override fun getStoresByBranch(branchId: String): Flow<List<Store>> = callbackFlow {
        val listener = firestore.collection("stores")
            .whereEqualTo("branchId", branchId)
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(StoreDto::class.java)?.toDomain()
                    } catch (_: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override fun getAllStores(): Flow<List<Store>> = callbackFlow {
        val listener = firestore.collection("stores")
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(StoreDto::class.java)?.toDomain()
                    } catch (_: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addBranch(branch: Branch): Result<String> {
        val id = if (branch.branchId.isNotBlank()) branch.branchId else UUID.randomUUID().toString()
        val finalBranch = branch.copy(branchId = id)
        return try {
            firestore.collection("branches")
                .document(id)
                .set(finalBranch.toDto())
                .await()
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addStore(store: Store): Result<String> {
        val id = if (store.storeId.isNotBlank()) store.storeId else UUID.randomUUID().toString()
        val finalStore = store.copy(storeId = id)
        return try {
            firestore.collection("stores")
                .document(id)
                .set(finalStore.toDto())
                .await()
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
