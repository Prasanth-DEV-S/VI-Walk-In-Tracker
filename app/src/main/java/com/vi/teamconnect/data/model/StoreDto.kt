package com.vi.teamconnect.data.model

import com.google.firebase.firestore.PropertyName

data class StoreDto(
    @get:PropertyName("storeId") @set:PropertyName("storeId") var storeId: String = "",
    @get:PropertyName("storeName") @set:PropertyName("storeName") var storeName: String = "",
    @get:PropertyName("branchId") @set:PropertyName("branchId") var branchId: String = "",
    @get:PropertyName("branchName") @set:PropertyName("branchName") var branchName: String = "",
    @get:PropertyName("isActive") @set:PropertyName("isActive") var isActive: Boolean = true,
    @get:PropertyName("createdAt") @set:PropertyName("createdAt") var createdAt: Any? = null
)
