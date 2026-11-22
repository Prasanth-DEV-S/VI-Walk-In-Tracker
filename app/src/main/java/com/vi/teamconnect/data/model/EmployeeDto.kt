package com.vi.teamconnect.data.model

import com.google.firebase.firestore.PropertyName

data class EmployeeDto(
    @get:PropertyName("employeeUid") @set:PropertyName("employeeUid") var employeeUid: String = "",
    @get:PropertyName("employeeId") @set:PropertyName("employeeId") var employeeId: String = "",
    @get:PropertyName("phoneNumber") @set:PropertyName("phoneNumber") var phoneNumber: String = "",
    @get:PropertyName("employeeName") @set:PropertyName("employeeName") var employeeName: String = "",
    @get:PropertyName("branchId") @set:PropertyName("branchId") var branchId: String = "",
    @get:PropertyName("branchName") @set:PropertyName("branchName") var branchName: String = "",
    @get:PropertyName("role") @set:PropertyName("role") var role: String = "EMPLOYEE",
    @get:PropertyName("accountStatus") @set:PropertyName("accountStatus") var accountStatus: String = "PENDING_APPROVAL",
    @get:PropertyName("createdAt") @set:PropertyName("createdAt") var createdAt: Any? = null,
    @get:PropertyName("approvedAt") @set:PropertyName("approvedAt") var approvedAt: Any? = null,
    @get:PropertyName("approvedBy") @set:PropertyName("approvedBy") var approvedBy: String? = null,

    // Legacy fallback fields
    @get:PropertyName("uid") @set:PropertyName("uid") var uid: String? = null,
    @get:PropertyName("fullName") @set:PropertyName("fullName") var fullName: String? = null,
    @get:PropertyName("email") @set:PropertyName("email") var email: String? = null
)
