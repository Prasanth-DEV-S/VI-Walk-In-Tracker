package com.vi.teamconnect.domain.model

data class CustomerFilter(
    val searchQuery: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val creName: String = "",
    val purposeOfVisit: String = "",
    val prePp: String = "",
    val operator: String = "",
    val happyCalling: String = "",
    val referral: String = "",
    val sortBy: SortBy = SortBy.LATEST_DATE
)

enum class SortBy {
    LATEST_DATE,
    OLDEST_DATE,
    CUSTOMER_NAME,
    CRE_NAME
}
