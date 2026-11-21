package com.vi.teamconnect.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vi.teamconnect.data.local.UserPreferences
import com.vi.teamconnect.data.mapper.toDomain
import com.vi.teamconnect.data.mapper.toDto
import com.vi.teamconnect.data.model.EmployeeDto
import com.vi.teamconnect.domain.model.AccountStatus
import com.vi.teamconnect.domain.model.Employee
import com.vi.teamconnect.domain.model.Role
import com.vi.teamconnect.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userPreferences: UserPreferences
) : AuthRepository {

    override val currentUserId: String?
        get() = auth.currentUser?.uid

    override val currentUserFlow: Flow<Employee?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val uid = firebaseAuth.currentUser?.uid
            if (uid == null) {
                trySend(null)
            } else {
                firestore.collection("employees").document(uid)
                    .addSnapshotListener { snapshot, _ ->
                        val dto = snapshot?.toObject(EmployeeDto::class.java)
                        trySend(dto?.toDomain())
                    }
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun login(email: String, password: String): Result<Employee> {
        return try {
            val cleanEmail = email.trim()
            val authResult = auth.signInWithEmailAndPassword(cleanEmail, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Authentication failed. Invalid user.")

            val doc = firestore.collection("employees").document(uid).get().await()
            val employeeDto = doc.toObject(EmployeeDto::class.java)
                ?: throw Exception("Employee profile not found in database.")

            val employee = employeeDto.toDomain()

            when (employee.accountStatus) {
                AccountStatus.ACTIVE -> {
                    userPreferences.saveUserSession(employee.uid, employee.employeeId, employee.role.name)
                    Result.success(employee)
                }
                AccountStatus.PENDING_APPROVAL -> {
                    auth.signOut()
                    Result.failure(Exception("Account is pending Admin approval."))
                }
                AccountStatus.REJECTED -> {
                    auth.signOut()
                    Result.failure(Exception("Registration request was rejected by Admin."))
                }
                AccountStatus.SUSPENDED -> {
                    auth.signOut()
                    Result.failure(Exception("Account is suspended by Admin."))
                }
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Login failed. Please check Email and Password."))
        }
    }

    override suspend fun register(
        email: String,
        employeeId: String,
        fullName: String,
        phoneNumber: String,
        password: String
    ): Result<Employee> {
        return try {
            val cleanEmail = email.trim()
            val cleanEmpId = employeeId.trim()

            // Check if Employee ID already exists in Firestore
            val existingQuery = firestore.collection("employees")
                .whereEqualTo("employeeId", cleanEmpId)
                .get().await()

            if (!existingQuery.isEmpty) {
                return Result.failure(Exception("Employee ID '$cleanEmpId' is already registered."))
            }

            val authResult = auth.createUserWithEmailAndPassword(cleanEmail, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Registration failed.")

            val newEmployee = Employee(
                uid = uid,
                employeeId = cleanEmpId,
                fullName = fullName.trim(),
                email = cleanEmail,
                phoneNumber = phoneNumber.trim(),
                role = Role.EMPLOYEE,
                accountStatus = AccountStatus.PENDING_APPROVAL,
                createdAt = System.currentTimeMillis()
            )

            firestore.collection("employees").document(uid)
                .set(newEmployee.toDto())
                .await()

            // Sign out immediately so unapproved user cannot access app
            auth.signOut()

            Result.success(newEmployee)
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Registration failed."))
        }
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Failed to send password reset email."))
        }
    }

    override suspend fun logout() {
        auth.signOut()
        userPreferences.clearSession()
    }

    override suspend fun getCurrentEmployee(): Employee? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val doc = firestore.collection("employees").document(uid).get().await()
            doc.toObject(EmployeeDto::class.java)?.toDomain()
        } catch (e: Exception) {
            null
        }
    }
}
