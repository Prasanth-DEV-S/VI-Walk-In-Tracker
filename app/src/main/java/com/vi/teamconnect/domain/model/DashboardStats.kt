package com.vi.teamconnect.domain.model

data class DashboardStats(
    val totalCustomerRecords: Int = 0,
    val todaysVisits: Int = 0,
    val totalCres: Int = 0,
    val happyCallingYes: Int = 0,
    val pendingFollowups: Int = 0,
    val monthlyRecords: Int = 0
)
