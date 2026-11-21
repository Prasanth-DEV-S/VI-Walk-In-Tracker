package com.vi.teamconnect.domain.repository

import com.vi.teamconnect.domain.model.CustomerFilter
import com.vi.teamconnect.domain.model.CustomerVisit
import com.vi.teamconnect.domain.model.CustomerVisitHistory
import com.vi.teamconnect.domain.model.DashboardStats
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    fun getCustomerVisits(filter: CustomerFilter): Flow<List<CustomerVisit>>
    fun getCustomerVisitById(recordId: String): Flow<CustomerVisit?>
    fun getDashboardStats(): Flow<DashboardStats>

    suspend fun addCustomerVisit(visit: CustomerVisit): Result<String>
    suspend fun updateCustomerVisit(visit: CustomerVisit): Result<Unit>
    suspend fun deleteCustomerVisit(recordId: String): Result<Unit> // Soft delete
    suspend fun recoverCustomerVisit(recordId: String): Result<Unit>

    fun getVisitHistory(recordId: String): Flow<List<CustomerVisitHistory>>
    suspend fun generateToken(): String
}
