package com.vi.teamconnect.domain.repository

import com.vi.teamconnect.domain.model.Employee
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUserId: String?
    val currentUserFlow: Flow<Employee?>

    suspend fun login(email: String, password: String): Result<Employee>
    suspend fun register(
        email: String,
        employeeId: String,
        fullName: String,
        phoneNumber: String,
        password: String
    ): Result<Employee>

    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun logout()
    suspend fun getCurrentEmployee(): Employee?
}
