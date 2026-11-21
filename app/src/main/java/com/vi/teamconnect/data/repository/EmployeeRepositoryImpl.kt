package com.vi.teamconnect.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.vi.teamconnect.data.mapper.toDomain
import com.vi.teamconnect.data.model.EmployeeDto
import com.vi.teamconnect.domain.model.AccountStatus
import com.vi.teamconnect.domain.model.Employee
import com.vi.teamconnect.domain.model.Role
import com.vi.teamconnect.domain.repository.EmployeeRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmployeeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : EmployeeRepository {

    override fun getAllEmployees(): Flow<List<Employee>> = callbackFlow {
        val listener = firestore.collection("employees")
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(EmployeeDto::class.java)?.toDomain()
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override fun getPendingApprovalEmployees(): Flow<List<Employee>> = callbackFlow {
        val listener = firestore.collection("employees")
            .whereEqualTo("accountStatus", AccountStatus.PENDING_APPROVAL.name)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(EmployeeDto::class.java)?.toDomain()
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun approveEmployee(uid: String, adminName: String): Result<Unit> {
        return try {
            firestore.collection("employees")
                .document(uid)
                .update(
                    mapOf(
                        "accountStatus" to AccountStatus.ACTIVE.name,
                        "approvedAt" to System.currentTimeMillis(),
                        "approvedBy" to adminName
                    )
                ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Failed to approve employee."))
        }
    }

    override suspend fun rejectEmployee(uid: String): Result<Unit> {
        return try {
            firestore.collection("employees")
                .document(uid)
                .update("accountStatus", AccountStatus.REJECTED.name)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Failed to reject employee."))
        }
    }

    override suspend fun updateEmployeeRole(uid: String, role: Role): Result<Unit> {
        return try {
            firestore.collection("employees")
                .document(uid)
                .update("role", role.name)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Failed to update employee role."))
        }
    }

    override suspend fun updateEmployeeStatus(uid: String, status: AccountStatus): Result<Unit> {
        return try {
            firestore.collection("employees")
                .document(uid)
                .update("accountStatus", status.name)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Failed to update employee status."))
        }
    }

    override suspend fun updateEmployeeProfile(employee: Employee): Result<Unit> {
        return try {
            firestore.collection("employees")
                .document(employee.uid)
                .update(
                    mapOf(
                        "fullName" to employee.fullName,
                        "phoneNumber" to employee.phoneNumber,
                        "employeeId" to employee.employeeId
                    )
                ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Failed to update employee profile."))
        }
    }
}
