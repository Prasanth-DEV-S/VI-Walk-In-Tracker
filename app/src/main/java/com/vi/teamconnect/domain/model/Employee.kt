package com.vi.teamconnect.domain.model

enum class Role {
    EMPLOYEE,
    TL,
    MANAGER,
    ADMIN,
    MASTER_ADMIN
}

enum class AccountStatus {
    PENDING_APPROVAL,
    ACTIVE,
    REJECTED,
    SUSPENDED
}

data class Employee(
    val employeeUid: String = "",
    val employeeId: String = "",
    val phoneNumber: String = "",
    val employeeName: String = "",
    val branchId: String = "",
    val branchName: String = "",
    val role: Role = Role.EMPLOYEE,
    val accountStatus: AccountStatus = AccountStatus.PENDING_APPROVAL,
    val createdAt: Long = System.currentTimeMillis(),
    val approvedAt: Long? = null,
    val approvedBy: String? = null
) {
    // Backwards compatibility aliases
    val uid: String get() = employeeUid
    val fullName: String get() = employeeName
    val email: String get() = ""
}
