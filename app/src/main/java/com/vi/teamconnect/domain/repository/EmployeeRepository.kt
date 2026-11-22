package com.vi.teamconnect.domain.repository

import com.vi.teamconnect.domain.model.AccountStatus
import com.vi.teamconnect.domain.model.Employee
import com.vi.teamconnect.domain.model.Role
import kotlinx.coroutines.flow.Flow

interface EmployeeRepository {
    fun getAllEmployees(): Flow<List<Employee>>
    fun getEmployeesByBranch(branchId: String): Flow<List<Employee>>
    fun getPendingApprovalEmployees(): Flow<List<Employee>>

    suspend fun approveEmployee(uid: String, adminName: String): Result<Unit>
    suspend fun rejectEmployee(uid: String): Result<Unit>
    suspend fun updateEmployeeRole(uid: String, role: Role): Result<Unit>
    suspend fun updateEmployeeStatus(uid: String, status: AccountStatus): Result<Unit>
    suspend fun updateEmployeeProfile(employee: Employee): Result<Unit>
}
