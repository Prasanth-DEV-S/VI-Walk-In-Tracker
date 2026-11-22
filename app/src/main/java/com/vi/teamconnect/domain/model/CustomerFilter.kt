package com.vi.teamconnect.domain.model

enum class DateRangeOption(val label: String) {
    ALL("All Time"),
    TODAY("Today"),
    YESTERDAY("Yesterday"),
    LAST_7_DAYS("Last 7 Days"),
    LAST_30_DAYS("Last 30 Days"),
    CUSTOM("Custom Date Range")
}

data class CustomerFilter(
    val searchQuery: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val dateRange: DateRangeOption = DateRangeOption.ALL,
    val branchId: String = "",
    val employeeUid: String = "",
    val storeId: String = "",
    val prePost: String = "", // PREPAID / POSTPAID
    val gmb: String = "", // YES / NO
    val tagging: String = "", // YES / NO
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
