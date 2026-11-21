package com.vi.teamconnect.data.model

import com.google.firebase.firestore.PropertyName

/**
 * CustomerVisitDto maps all 15 business fields cleanly to Firestore.
 */
data class CustomerVisitDto(
    @get:PropertyName("recordId") @set:PropertyName("recordId") var recordId: String = "",
    @get:PropertyName("date") @set:PropertyName("date") var date: String = "",
    @get:PropertyName("token") @set:PropertyName("token") var token: String = "",
    @get:PropertyName("creName") @set:PropertyName("creName") var creName: String = "",
    @get:PropertyName("creEmployeeId") @set:PropertyName("creEmployeeId") var creEmployeeId: String = "",
    @get:PropertyName("cusName") @set:PropertyName("cusName") var cusName: String = "",
    @get:PropertyName("mobNo") @set:PropertyName("mobNo") var mobNo: String = "",
    @get:PropertyName("purposeOfVisit") @set:PropertyName("purposeOfVisit") var purposeOfVisit: String = "",
    @get:PropertyName("prePp") @set:PropertyName("prePp") var prePp: String = "PRE",
    @get:PropertyName("alterNo") @set:PropertyName("alterNo") var alterNo: String = "",
    @get:PropertyName("srNo") @set:PropertyName("srNo") var srNo: String = "",
    @get:PropertyName("operator") @set:PropertyName("operator") var operator: String = "Vi",
    @get:PropertyName("gbp") @set:PropertyName("gbp") var gbp: String = "",
    @get:PropertyName("remarks") @set:PropertyName("remarks") var remarks: String = "",
    @get:PropertyName("happyCalling") @set:PropertyName("happyCalling") var happyCalling: String = "Pending",
    @get:PropertyName("did") @set:PropertyName("did") var did: String = "",
    @get:PropertyName("refferel") @set:PropertyName("refferel") var refferel: String = "",

    // Metadata
    @get:PropertyName("createdBy") @set:PropertyName("createdBy") var createdBy: String = "",
    @get:PropertyName("createdAt") @set:PropertyName("createdAt") var createdAt: Long = System.currentTimeMillis(),
    @get:PropertyName("updatedBy") @set:PropertyName("updatedBy") var updatedBy: String = "",
    @get:PropertyName("updatedAt") @set:PropertyName("updatedAt") var updatedAt: Long = System.currentTimeMillis(),
    @get:PropertyName("isDeleted") @set:PropertyName("isDeleted") var isDeleted: Boolean = false,
    @get:PropertyName("deletedBy") @set:PropertyName("deletedBy") var deletedBy: String? = null,
    @get:PropertyName("deletedAt") @set:PropertyName("deletedAt") var deletedAt: Long? = null
)
