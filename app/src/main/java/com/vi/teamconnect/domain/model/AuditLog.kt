package com.vi.teamconnect.domain.model

data class AuditLog(
    val logId: String = "",
    val employeeUid: String = "",
    val employeeName: String = "",
    val action: String = "",
    val recordId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
