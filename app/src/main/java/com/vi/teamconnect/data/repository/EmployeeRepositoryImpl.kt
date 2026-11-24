package com.vi.teamconnect.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
                val firestoreList = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val dto = doc.toObject(EmployeeDto::class.java)
                        val domain = dto?.toDomain()
                        if (domain != null && domain.employeeUid.isBlank()) {
                            domain.copy(employeeUid = doc.id)
                        } else {
                            domain
                        }
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(firestoreList)
            }
        awaitClose { listener.remove() }
    }

    override fun getEmployeesByBranch(branchId: String): Flow<List<Employee>> = callbackFlow {
        val listener = firestore.collection("employees")
            .whereEqualTo("branchId", branchId)
            .addSnapshotListener { snapshot, _ ->
                val firestoreList = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val dto = doc.toObject(EmployeeDto::class.java)
                        val domain = dto?.toDomain()
                        if (domain != null && domain.employeeUid.isBlank()) {
                            domain.copy(employeeUid = doc.id)
                        } else {
                            domain
                        }
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(firestoreList)
            }
        awaitClose { listener.remove() }
    }

    override fun getPendingApprovalEmployees(): Flow<List<Employee>> = callbackFlow {
        val listener = firestore.collection("employees")
            .addSnapshotListener { snapshot, _ ->
                val firestoreList = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val dto = doc.toObject(EmployeeDto::class.java)
                        val domain = dto?.toDomain()
                        val finalEmp = if (domain != null && domain.employeeUid.isBlank()) {
                            domain.copy(employeeUid = doc.id)
                        } else {
                            domain
                        }
                        if (finalEmp?.accountStatus == AccountStatus.PENDING_APPROVAL) finalEmp else null
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                trySend(firestoreList)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun approveEmployee(uid: String, adminName: String): Result<Unit> {
        return try {
            firestore.collection("employees")
                .document(uid)
                .set(
                    mapOf(
                        "accountStatus" to AccountStatus.ACTIVE.name,
                        "status" to AccountStatus.ACTIVE.name,
                        "approvedAt" to System.currentTimeMillis(),
                        "approvedBy" to adminName
                    ),
                    SetOptions.merge()
                ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun rejectEmployee(uid: String): Result<Unit> {
        return try {
            firestore.collection("employees")
                .document(uid)
                .set(
                    mapOf(
                        "accountStatus" to AccountStatus.REJECTED.name,
                        "status" to AccountStatus.REJECTED.name
                    ),
                    SetOptions.merge()
                ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateEmployeeRole(uid: String, role: Role): Result<Unit> {
        return try {
            firestore.collection("employees")
                .document(uid)
                .set(
                    mapOf("role" to role.name),
                    SetOptions.merge()
                ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateEmployeeStatus(uid: String, status: AccountStatus): Result<Unit> {
        return try {
            firestore.collection("employees")
                .document(uid)
                .set(
                    mapOf(
                        "accountStatus" to status.name,
                        "status" to status.name
                    ),
                    SetOptions.merge()
                ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateEmployeeProfile(employee: Employee): Result<Unit> {
        return try {
            firestore.collection("employees")
                .document(employee.employeeUid)
                .set(
                    mapOf(
                        "employeeName" to employee.employeeName,
                        "phoneNumber" to employee.phoneNumber,
                        "employeeId" to employee.employeeId,
                        "branchId" to employee.branchId,
                        "branchName" to employee.branchName,
                        "fullName" to employee.employeeName
                    ),
                    SetOptions.merge()
                ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
