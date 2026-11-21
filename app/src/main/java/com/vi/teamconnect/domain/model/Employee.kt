package com.vi.teamconnect.domain.model

enum class Role {
    ADMIN,
    EMPLOYEE
}

enum class AccountStatus {
    PENDING_APPROVAL,
    ACTIVE,
    REJECTED,
    SUSPENDED
}

data class Employee(
    val uid: String = "",
    val employeeId: String = "",
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val role: Role = Role.EMPLOYEE,
    val accountStatus: AccountStatus = AccountStatus.PENDING_APPROVAL,
    val createdAt: Long = System.currentTimeMillis(),
    val approvedAt: Long? = null,
    val approvedBy: String? = null
)
