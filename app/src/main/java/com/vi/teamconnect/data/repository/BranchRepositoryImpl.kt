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
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BranchRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : BranchRepository {

    private val staticBranches = CopyOnWriteArrayList<Branch>(
        listOf(
            Branch(branchId = "BRANCH-DEL-01", branchName = "Connaught Place Branch", branchCode = "DEL01"),
            Branch(branchId = "BRANCH-MUM-01", branchName = "Andheri West Branch", branchCode = "MUM01"),
            Branch(branchId = "BRANCH-BLR-01", branchName = "MG Road Branch", branchCode = "BLR01")
        )
    )

    private val staticStores = CopyOnWriteArrayList<Store>(
        listOf(
            Store(storeId = "STORE-CP-01", storeName = "Vi Store CP", branchId = "BRANCH-DEL-01", branchName = "Connaught Place Branch"),
            Store(storeId = "STORE-ANDHERI-01", storeName = "Vi Store Andheri", branchId = "BRANCH-MUM-01", branchName = "Andheri West Branch"),
            Store(storeId = "STORE-MGROAD-01", storeName = "Vi Store MG Road", branchId = "BRANCH-BLR-01", branchName = "MG Road Branch")
        )
    )

    override fun getAllBranches(): Flow<List<Branch>> = callbackFlow {
        val listener = firestore.collection("branches")
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(BranchDto::class.java)?.toDomain()
                    } catch (_: Exception) {
                        null
                    }
                } ?: emptyList()

                val combined = (list + staticBranches).distinctBy { it.branchId }
                trySend(combined)
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

                val combined = (list + staticStores.filter { it.branchId == branchId }).distinctBy { it.storeId }
                trySend(combined)
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

                val combined = (list + staticStores).distinctBy { it.storeId }
                trySend(combined)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addBranch(branch: Branch): Result<String> {
        val id = if (branch.branchId.isNotBlank()) branch.branchId else UUID.randomUUID().toString()
        val finalBranch = branch.copy(branchId = id)
        staticBranches.add(finalBranch)
        return try {
            firestore.collection("branches")
                .document(id)
                .set(finalBranch.toDto())
                .await()
            Result.success(id)
        } catch (_: Exception) {
            Result.success(id)
        }
    }

    override suspend fun addStore(store: Store): Result<String> {
        val id = if (store.storeId.isNotBlank()) store.storeId else UUID.randomUUID().toString()
        val finalStore = store.copy(storeId = id)
        staticStores.add(finalStore)
        return try {
            firestore.collection("stores")
                .document(id)
                .set(finalStore.toDto())
                .await()
            Result.success(id)
        } catch (_: Exception) {
            Result.success(id)
        }
    }
}
