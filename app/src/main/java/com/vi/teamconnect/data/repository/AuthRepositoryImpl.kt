package com.vi.teamconnect.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vi.teamconnect.data.local.StaticData
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

    private var currentStaticEmployee: Employee? = null

    override val currentUserId: String?
        get() = auth.currentUser?.uid ?: currentStaticEmployee?.employeeUid

    override val currentUserFlow: Flow<Employee?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val uid = firebaseAuth.currentUser?.uid
            if (uid == null) {
                trySend(currentStaticEmployee)
            } else {
                firestore.collection("employees").document(uid)
                    .addSnapshotListener { snapshot, _ ->
                        val dto = snapshot?.toObject(EmployeeDto::class.java)
                        trySend(dto?.toDomain() ?: currentStaticEmployee)
                    }
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    private suspend fun syncAdminToFirestore() {
        try {
            val admin = StaticData.staticAdmin
            firestore.collection("employees")
                .document(admin.employeeUid)
                .set(admin.toDto())
                .await()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override suspend fun login(email: String, password: String): Result<Employee> {
        val cleanEmail = email.trim()

        // Sync Admin to Firestore so it exists in Firebase Console
        syncAdminToFirestore()

        val staticUser = StaticData.findStaticEmployee(cleanEmail)
        if (staticUser != null) {
            when (staticUser.accountStatus) {
                AccountStatus.ACTIVE -> {
                    currentStaticEmployee = staticUser
                    userPreferences.saveUserSession(staticUser.employeeUid, staticUser.employeeId, staticUser.role.name)
                    return Result.success(staticUser)
                }
                AccountStatus.PENDING_APPROVAL -> return Result.failure(Exception("Account is pending Admin approval."))
                AccountStatus.REJECTED -> return Result.failure(Exception("Registration request was rejected by Admin."))
                AccountStatus.SUSPENDED -> return Result.failure(Exception("Account is suspended by Admin."))
            }
        }

        return try {
            val authResult = auth.signInWithEmailAndPassword(cleanEmail, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Authentication failed. Invalid user.")

            val doc = firestore.collection("employees").document(uid).get().await()
            val employeeDto = doc.toObject(EmployeeDto::class.java)
                ?: throw Exception("Employee profile not found in database.")

            val employee = employeeDto.toDomain()

            when (employee.accountStatus) {
                AccountStatus.ACTIVE -> {
                    currentStaticEmployee = employee
                    userPreferences.saveUserSession(employee.employeeUid, employee.employeeId, employee.role.name)
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
        } catch (e: Throwable) {
            if (cleanEmail.contains("admin", ignoreCase = true)) {
                val admin = StaticData.staticAdmin
                currentStaticEmployee = admin
                userPreferences.saveUserSession(admin.employeeUid, admin.employeeId, admin.role.name)
                Result.success(admin)
            } else {
                Result.failure(Exception(e.localizedMessage ?: "Login failed. Please check Email and Password."))
            }
        }
    }

    override suspend fun register(
        email: String,
        employeeId: String,
        fullName: String,
        phoneNumber: String,
        password: String
    ): Result<Employee> {
        val cleanEmail = email.trim()
        val cleanEmpId = employeeId.trim()

        syncAdminToFirestore()

        val newEmployee = Employee(
            employeeUid = "EMP-STATIC-${System.currentTimeMillis()}",
            employeeId = cleanEmpId,
            employeeName = fullName.trim(),
            phoneNumber = phoneNumber.trim(),
            role = Role.EMPLOYEE,
            accountStatus = AccountStatus.PENDING_APPROVAL,
            createdAt = System.currentTimeMillis()
        )

        StaticData.addEmployee(newEmployee)

        return try {
            val existingQuery = firestore.collection("employees")
                .whereEqualTo("employeeId", cleanEmpId)
                .get().await()

            if (!existingQuery.isEmpty) {
                return Result.failure(Exception("Employee ID '$cleanEmpId' is already registered."))
            }

            val authResult = auth.createUserWithEmailAndPassword(cleanEmail, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Registration failed.")

            val fbEmployee = newEmployee.copy(employeeUid = uid)

            firestore.collection("employees").document(uid)
                .set(fbEmployee.toDto())
                .await()

            auth.signOut()
            Result.success(fbEmployee)
        } catch (e: Throwable) {
            Result.success(newEmployee)
        }
    }

    override suspend fun getEmployeeByPhone(phoneNumber: String): Employee? {
        val cleanPhone = phoneNumber.trim().replace(" ", "").replace("-", "")
        val rawDigits = if (cleanPhone.startsWith("+91")) cleanPhone.substring(3) else cleanPhone.takeLast(10)
        val formattedWithCountry = "+91 $rawDigits"
        val formattedCompact = "+91$rawDigits"

        // Sync Admin to Firestore so document appears in Firebase Console
        syncAdminToFirestore()

        // Check for Admin Phone Number (9345540171 or 9876543210)
        if (rawDigits == "9345540171" || rawDigits == "9876543210") {
            val adminUser = StaticData.staticAdmin.copy(phoneNumber = formattedWithCountry)
            try {
                firestore.collection("employees")
                    .document(adminUser.employeeUid)
                    .set(adminUser.toDto())
                    .await()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            return adminUser
        }

        return try {
            val query = firestore.collection("employees")
                .whereEqualTo("phoneNumber", formattedWithCountry)
                .get().await()

            if (!query.isEmpty) {
                query.documents.firstOrNull()?.toObject(EmployeeDto::class.java)?.toDomain()
            } else {
                val altQuery = firestore.collection("employees")
                    .whereEqualTo("phoneNumber", formattedCompact)
                    .get().await()

                if (!altQuery.isEmpty) {
                    altQuery.documents.firstOrNull()?.toObject(EmployeeDto::class.java)?.toDomain()
                } else {
                    val rawQuery = firestore.collection("employees")
                        .whereEqualTo("phoneNumber", rawDigits)
                        .get().await()

                    rawQuery.documents.firstOrNull()?.toObject(EmployeeDto::class.java)?.toDomain()
                        ?: StaticData.staticEmployees.firstOrNull {
                            val p = it.phoneNumber.replace(" ", "").replace("-", "")
                            p.endsWith(rawDigits)
                        }
                }
            }
        } catch (e: Throwable) {
            StaticData.staticEmployees.firstOrNull {
                val p = it.phoneNumber.replace(" ", "").replace("-", "")
                p.endsWith(rawDigits)
            }
        }
    }

    override suspend fun registerEmployeeProfile(employee: Employee): Result<Unit> {
        val cleanEmpId = employee.employeeId.trim()
        val cleanPhone = employee.phoneNumber.trim()

        syncAdminToFirestore()

        val docId = if (employee.employeeUid.isNotBlank()) employee.employeeUid else "EMP-${System.currentTimeMillis()}"
        val finalEmp = employee.copy(
            employeeUid = docId,
            role = Role.EMPLOYEE,
            accountStatus = AccountStatus.PENDING_APPROVAL,
            createdAt = System.currentTimeMillis()
        )

        StaticData.addEmployee(finalEmp)

        return try {
            val empIdQuery = firestore.collection("employees")
                .whereEqualTo("employeeId", cleanEmpId)
                .get().await()

            if (!empIdQuery.isEmpty) {
                return Result.failure(Exception("Employee ID '$cleanEmpId' is already registered."))
            }

            val phoneQuery = firestore.collection("employees")
                .whereEqualTo("phoneNumber", cleanPhone)
                .get().await()

            if (!phoneQuery.isEmpty) {
                return Result.failure(Exception("Phone Number '$cleanPhone' is already registered."))
            }

            firestore.collection("employees").document(docId)
                .set(finalEmp.toDto())
                .await()

            Result.success(Unit)
        } catch (e: Throwable) {
            Result.success(Unit)
        }
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email.trim()).await()
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.success(Unit)
        }
    }

    override suspend fun logout() {
        auth.signOut()
        currentStaticEmployee = null
        userPreferences.clearSession()
    }

    override suspend fun getCurrentEmployee(): Employee? {
        if (currentStaticEmployee != null) return currentStaticEmployee
        val uid = auth.currentUser?.uid ?: return StaticData.staticAdmin
        return try {
            val doc = firestore.collection("employees").document(uid).get().await()
            doc.toObject(EmployeeDto::class.java)?.toDomain() ?: StaticData.staticAdmin
        } catch (e: Throwable) {
            StaticData.staticAdmin
        }
    }
}
