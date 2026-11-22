package com.vi.teamconnect.domain.model

/**
 * CustomerVisit domain model representing walk-in customer records in Firestore.
 *
 * Business Fields:
 * 1. StoreName / storeId / storeName
 * 2. Date (YYYY-MM-DD)
 * 3. CRE Name / creUid / creName
 * 4. CUSTOMER NAME (customerName)
 * 5. MOB NUMBER (mobileNumber)
 * 6. PRE/POST (prePost: "PREPAID" / "POSTPAID")
 * 7. ALT NUMBER (altNumber)
 * 8. REASON FOR VISIT (reasonForVisit)
 * 9. GMB ("YES" / "NO")
 * 10. TAGGING ("YES" / "NO")
 */
data class CustomerVisit(
    val recordId: String = "",
    val storeId: String = "",
    val storeName: String = "",
    val branchId: String = "",
    val branchName: String = "",
    val date: String = "",
    val creUid: String = "",
    val creName: String = "",
    val customerName: String = "",
    val mobileNumber: String = "",
    val prePost: String = "PREPAID", // PREPAID / POSTPAID
    val altNumber: String = "",
    val reasonForVisit: String = "",
    val gmb: String = "NO", // YES / NO
    val tagging: String = "NO", // YES / NO

    // Metadata
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedBy: String = "",
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false
) {
    // Aliases for legacy code compatibility
    val cusName: String get() = customerName
    val mobNo: String get() = mobileNumber
    val prePp: String get() = if (prePost.equals("POSTPAID", ignoreCase = true)) "PP" else "PRE"
    val alterNo: String get() = altNumber
    val purposeOfVisit: String get() = reasonForVisit
    val creEmployeeId: String get() = creUid
    val token: String get() = recordId.takeLast(8)
    val operator: String get() = "Vi"
    val gbp: String get() = gmb
    val remarks: String get() = reasonForVisit
    val happyCalling: String get() = "N/A"
    val did: String get() = ""
    val refferel: String get() = ""
}
