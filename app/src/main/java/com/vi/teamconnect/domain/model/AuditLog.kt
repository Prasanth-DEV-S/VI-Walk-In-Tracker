package com.vi.teamconnect.domain.model

data class AuditLog(
    val logId: String = "",
    val action: String = "",
    val recordId: String = "",
    val employeeUid: String = "",
    val branchId: String = "",
    val changedFields: Map<String, String> = emptyMap(),
    val createdAt: Long = System.currentTimeMillis()
)
