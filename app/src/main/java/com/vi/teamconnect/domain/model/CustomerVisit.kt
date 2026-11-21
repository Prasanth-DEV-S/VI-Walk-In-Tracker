package com.vi.teamconnect.domain.model

/**
 * CustomerVisit domain model representing all 15 business fields from the customer visit register,
 * along with necessary internal metadata for auditing and soft-deletion.
 *
 * Business Fields:
 * 1. DATE (date)
 * 2. TOKEN (token)
 * 3. CRE NAME (creName)
 * 4. CUS NAME (cusName)
 * 5. MOB NO (mobNo)
 * 6. PURPOSE OF VISIT (purposeOfVisit)
 * 7. PRE/PP (prePp)
 * 8. ALTER NO (alterNo)
 * 9. SR NO (srNo)
 * 10. OPERATOR (operator)
 * 11. GBP (gbp)
 * 12. Remarks (remarks)
 * 13. Happy calling (happyCalling)
 * 14. DID (did)
 * 15. Refferel (refferel)
 */
data class CustomerVisit(
    val recordId: String = "",
    val date: String = "", // YYYY-MM-DD
    val token: String = "",
    val creName: String = "",
    val creEmployeeId: String = "",
    val cusName: String = "",
    val mobNo: String = "",
    val purposeOfVisit: String = "",
    val prePp: String = "PRE", // PRE / PP
    val alterNo: String = "",
    val srNo: String = "",
    val operator: String = "Vi", // Vi, Airtel, Jio, BSNL, Other
    val gbp: String = "",
    val remarks: String = "",
    val happyCalling: String = "Pending", // Yes, No, Pending, Not Applicable
    val did: String = "",
    val refferel: String = "",

    // Audit Metadata
    val createdBy: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedBy: String = "",
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val deletedBy: String? = null,
    val deletedAt: Long? = null
)
