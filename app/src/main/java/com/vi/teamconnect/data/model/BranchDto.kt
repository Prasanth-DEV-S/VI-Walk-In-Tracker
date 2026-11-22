package com.vi.teamconnect.data.model

import com.google.firebase.firestore.PropertyName

data class BranchDto(
    @get:PropertyName("branchId") @set:PropertyName("branchId") var branchId: String = "",
    @get:PropertyName("branchName") @set:PropertyName("branchName") var branchName: String = "",
    @get:PropertyName("branchCode") @set:PropertyName("branchCode") var branchCode: String = "",
    @get:PropertyName("address") @set:PropertyName("address") var address: String = "",
    @get:PropertyName("managerUid") @set:PropertyName("managerUid") var managerUid: String = "",
    @get:PropertyName("isActive") @set:PropertyName("isActive") var isActive: Boolean = true,
    @get:PropertyName("createdAt") @set:PropertyName("createdAt") var createdAt: Any? = null
)
