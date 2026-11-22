package com.vi.teamconnect.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.vi.teamconnect.data.local.StaticData
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
                        doc.toObject(EmployeeDto::class.java)?.toDomain()
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                val combined = (firestoreList + StaticData.staticEmployees).distinctBy { it.employeeUid }
                trySend(combined)
            }
        awaitClose { listener.remove() }
    }

    override fun getEmployeesByBranch(branchId: String): Flow<List<Employee>> = callbackFlow {
        val listener = firestore.collection("employees")
            .whereEqualTo("branchId", branchId)
            .addSnapshotListener { snapshot, _ ->
                val firestoreList = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(EmployeeDto::class.java)?.toDomain()
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                val combined = (firestoreList + StaticData.staticEmployees.filter { it.branchId == branchId })
                    .distinctBy { it.employeeUid }
                trySend(combined)
            }
        awaitClose { listener.remove() }
    }

    override fun getPendingApprovalEmployees(): Flow<List<Employee>> = callbackFlow {
        val listener = firestore.collection("employees")
            .whereEqualTo("accountStatus", AccountStatus.PENDING_APPROVAL.name)
            .addSnapshotListener { snapshot, _ ->
                val firestoreList = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(EmployeeDto::class.java)?.toDomain()
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                val combined = (firestoreList + StaticData.staticEmployees.filter { it.accountStatus == AccountStatus.PENDING_APPROVAL })
                    .distinctBy { it.employeeUid }
                trySend(combined)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun approveEmployee(uid: String, adminName: String): Result<Unit> {
        StaticData.approveEmployee(uid, adminName)
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
        } catch (_: Exception) {
            Result.success(Unit)
        }
    }

    override suspend fun rejectEmployee(uid: String): Result<Unit> {
        StaticData.rejectEmployee(uid)
        return try {
            firestore.collection("employees")
                .document(uid)
                .update("accountStatus", AccountStatus.REJECTED.name)
                .await()

            Result.success(Unit)
        } catch (_: Exception) {
            Result.success(Unit)
        }
    }

    override suspend fun updateEmployeeRole(uid: String, role: Role): Result<Unit> {
        StaticData.updateEmployeeRole(uid, role)
        return try {
            firestore.collection("employees")
                .document(uid)
                .update("role", role.name)
                .await()

            Result.success(Unit)
        } catch (_: Exception) {
            Result.success(Unit)
        }
    }

    override suspend fun updateEmployeeStatus(uid: String, status: AccountStatus): Result<Unit> {
        StaticData.updateEmployeeStatus(uid, status)
        return try {
            firestore.collection("employees")
                .document(uid)
                .update("accountStatus", status.name)
                .await()

            Result.success(Unit)
        } catch (_: Exception) {
            Result.success(Unit)
        }
    }

    override suspend fun updateEmployeeProfile(employee: Employee): Result<Unit> {
        StaticData.updateEmployeeProfile(employee)
        return try {
            firestore.collection("employees")
                .document(employee.employeeUid)
                .update(
                    mapOf(
                        "employeeName" to employee.employeeName,
                        "phoneNumber" to employee.phoneNumber,
                        "employeeId" to employee.employeeId,
                        "branchId" to employee.branchId,
                        "branchName" to employee.branchName,
                        "fullName" to employee.employeeName
                    )
                ).await()

            Result.success(Unit)
        } catch (_: Exception) {
            Result.success(Unit)
        }
    }
}
