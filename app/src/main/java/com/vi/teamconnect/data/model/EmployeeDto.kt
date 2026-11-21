package com.vi.teamconnect.data.model

import com.google.firebase.firestore.PropertyName

data class EmployeeDto(
    @get:PropertyName("uid") @set:PropertyName("uid") var uid: String = "",
    @get:PropertyName("employeeId") @set:PropertyName("employeeId") var employeeId: String = "",
    @get:PropertyName("fullName") @set:PropertyName("fullName") var fullName: String = "",
    @get:PropertyName("email") @set:PropertyName("email") var email: String = "",
    @get:PropertyName("phoneNumber") @set:PropertyName("phoneNumber") var phoneNumber: String = "",
    @get:PropertyName("role") @set:PropertyName("role") var role: String = "EMPLOYEE",
    @get:PropertyName("accountStatus") @set:PropertyName("accountStatus") var accountStatus: String = "PENDING_APPROVAL",
    @get:PropertyName("createdAt") @set:PropertyName("createdAt") var createdAt: Long = System.currentTimeMillis(),
    @get:PropertyName("approvedAt") @set:PropertyName("approvedAt") var approvedAt: Long? = null,
    @get:PropertyName("approvedBy") @set:PropertyName("approvedBy") var approvedBy: String? = null
)
