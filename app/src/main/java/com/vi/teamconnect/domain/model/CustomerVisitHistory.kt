package com.vi.teamconnect.domain.model

data class CustomerVisitHistory(
    val historyId: String = "",
    val recordId: String = "",
    val action: String = "", // CREATE, UPDATE, DELETE, RECOVER
    val changedFields: Map<String, String> = emptyMap(),
    val changedBy: String = "",
    val changedAt: Long = System.currentTimeMillis()
)
