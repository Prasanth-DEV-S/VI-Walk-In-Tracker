package com.vi.teamconnect.domain.model

data class AppSettings(
    val settingId: String = "dropdown_options",
    val purposeOptions: List<String> = listOf(
        "SIM Swap", "New Connection", "Billing / Recharge", "MNP Port-in",
        "E-SIM Request", "Corporate Plan", "Complaint / Query", "Other"
    ),
    val operatorOptions: List<String> = listOf(
        "Vi", "Airtel", "Jio", "BSNL", "Other"
    ),
    val prePpOptions: List<String> = listOf(
        "PRE", "PP"
    ),
    val happyCallingOptions: List<String> = listOf(
        "Yes", "No", "Pending", "Not Applicable"
    ),
    val referralOptions: List<String> = listOf(
        "Self", "Existing Customer", "Retailer", "Employee Referral", "Social Media", "Walk-in", "Other"
    )
)
