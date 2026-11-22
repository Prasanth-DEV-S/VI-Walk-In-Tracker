package com.vi.teamconnect.domain.model

data class Store(
    val storeId: String = "",
    val storeName: String = "",
    val branchId: String = "",
    val branchName: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
