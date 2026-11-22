package com.vi.teamconnect.domain.repository

import com.vi.teamconnect.domain.model.Branch
import com.vi.teamconnect.domain.model.Store
import kotlinx.coroutines.flow.Flow

interface BranchRepository {
    fun getAllBranches(): Flow<List<Branch>>
    fun getStoresByBranch(branchId: String): Flow<List<Store>>
    fun getAllStores(): Flow<List<Store>>
    suspend fun addBranch(branch: Branch): Result<String>
    suspend fun addStore(store: Store): Result<String>
}
