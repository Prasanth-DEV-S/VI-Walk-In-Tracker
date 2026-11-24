package com.vi.teamconnect.data.model

import com.google.firebase.firestore.PropertyName

data class CustomerVisitDto(
    @get:PropertyName("recordId") @set:PropertyName("recordId") var recordId: String = "",
    @get:PropertyName("storeId") @set:PropertyName("storeId") var storeId: String = "",
    @get:PropertyName("storeName") @set:PropertyName("storeName") var storeName: String = "",
    @get:PropertyName("branchId") @set:PropertyName("branchId") var branchId: String = "",
    @get:PropertyName("branchName") @set:PropertyName("branchName") var branchName: String = "",
    @get:PropertyName("date") @set:PropertyName("date") var date: String = "",
    @get:PropertyName("creUid") @set:PropertyName("creUid") var creUid: String = "",
    @get:PropertyName("creEmployeeId") @set:PropertyName("creEmployeeId") var creEmployeeId: String? = null,
    @get:PropertyName("creName") @set:PropertyName("creName") var creName: String = "",
    @get:PropertyName("customerName") @set:PropertyName("customerName") var customerName: String = "",
    @get:PropertyName("mobileNumber") @set:PropertyName("mobileNumber") var mobileNumber: String = "",
    @get:PropertyName("prePost") @set:PropertyName("prePost") var prePost: String = "PREPAID",
    @get:PropertyName("altNumber") @set:PropertyName("altNumber") var altNumber: String = "",
    @get:PropertyName("reasonForVisit") @set:PropertyName("reasonForVisit") var reasonForVisit: String = "",
    @get:PropertyName("remarks") @set:PropertyName("remarks") var remarks: String = "",
    @get:PropertyName("gmb") @set:PropertyName("gmb") var gmb: String = "NO",
    @get:PropertyName("tagging") @set:PropertyName("tagging") var tagging: String = "NO",

    // Metadata
    @get:PropertyName("createdBy") @set:PropertyName("createdBy") var createdBy: String = "",
    @get:PropertyName("createdAt") @set:PropertyName("createdAt") var createdAt: Any? = null,
    @get:PropertyName("updatedBy") @set:PropertyName("updatedBy") var updatedBy: String = "",
    @get:PropertyName("updatedAt") @set:PropertyName("updatedAt") var updatedAt: Any? = null,
    @get:PropertyName("isDeleted") @set:PropertyName("isDeleted") var isDeleted: Boolean = false,
    @get:PropertyName("deletedBy") @set:PropertyName("deletedBy") var deletedBy: Any? = null,
    @get:PropertyName("deletedAt") @set:PropertyName("deletedAt") var deletedAt: Any? = null,

    // Legacy fallbacks
    @get:PropertyName("cusName") @set:PropertyName("cusName") var cusName: String? = null,
    @get:PropertyName("mobNo") @set:PropertyName("mobNo") var mobNo: String? = null,
    @get:PropertyName("prePp") @set:PropertyName("prePp") var prePp: String? = null,
    @get:PropertyName("alterNo") @set:PropertyName("alterNo") var alterNo: String? = null,
    @get:PropertyName("purposeOfVisit") @set:PropertyName("purposeOfVisit") var purposeOfVisit: String? = null,
    @get:PropertyName("did") @set:PropertyName("did") var did: String? = null,
    @get:PropertyName("gbp") @set:PropertyName("gbp") var gbp: String? = null,
    @get:PropertyName("happyCalling") @set:PropertyName("happyCalling") var happyCalling: String? = null,
    @get:PropertyName("operator") @set:PropertyName("operator") var operator: String? = null,
    @get:PropertyName("refferel") @set:PropertyName("refferel") var refferel: String? = null,
    @get:PropertyName("srNo") @set:PropertyName("srNo") var srNo: String? = null,
    @get:PropertyName("token") @set:PropertyName("token") var token: String? = null
)
