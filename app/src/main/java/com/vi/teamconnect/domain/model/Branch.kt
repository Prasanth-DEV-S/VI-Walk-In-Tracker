package com.vi.teamconnect.domain.model

data class Branch(
    val branchId: String = "",
    val branchName: String = "",
    val branchCode: String = "",
    val address: String = "",
    val managerUid: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
