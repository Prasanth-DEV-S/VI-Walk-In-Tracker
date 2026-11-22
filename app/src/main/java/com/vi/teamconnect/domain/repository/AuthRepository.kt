package com.vi.teamconnect.domain.repository

import com.vi.teamconnect.domain.model.Employee
import kotlinx.coroutines.flow.Flow
import android.app.Activity
import com.google.firebase.auth.FirebaseUser

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

    suspend fun getEmployeeByPhone(phoneNumber: String): Employee?
    suspend fun registerEmployeeProfile(employee: Employee): Result<Unit>

    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun logout()
    suspend fun getCurrentEmployee(): Employee?
}
