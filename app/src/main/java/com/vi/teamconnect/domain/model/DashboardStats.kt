package com.vi.teamconnect.domain.model

data class DashboardStats(
    val totalCustomerVisits: Int = 0,
    val todaysCustomerVisits: Int = 0,
    val totalBranches: Int = 0,
    val totalEmployees: Int = 0,
    val prepaidCount: Int = 0,
    val postpaidCount: Int = 0,
    val gmbYesCount: Int = 0,
    val taggingYesCount: Int = 0
) {
    // Aliases for legacy screens
    val totalCustomerRecords: Int get() = totalCustomerVisits
    val todaysVisits: Int get() = todaysCustomerVisits
    val totalCres: Int get() = totalEmployees
    val happyCallingYes: Int get() = gmbYesCount
    val pendingFollowups: Int get() = taggingYesCount
    val monthlyRecords: Int get() = totalCustomerVisits
}

data class BranchPerformance(
    val branchId: String = "",
    val branchName: String = "",
    val totalVisits: Int = 0,
    val prepaidCount: Int = 0,
    val postpaidCount: Int = 0,
    val gmbYesCount: Int = 0,
    val taggingYesCount: Int = 0
)

data class EmployeePerformance(
    val employeeUid: String = "",
    val employeeName: String = "",
    val employeeId: String = "",
    val branchName: String = "",
    val totalEntries: Int = 0,
    val todaysEntries: Int = 0,
    val prepaidCount: Int = 0,
    val postpaidCount: Int = 0,
    val gmbYesCount: Int = 0,
    val taggingYesCount: Int = 0
)
