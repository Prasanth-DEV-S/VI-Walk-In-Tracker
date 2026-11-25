package com.vi.teamconnect.domain.model

data class AppSettings(
    val settingId: String = "dropdown_options",
    val branchName: List<String> = listOf(
        "AS TRADERS",
        "GLOBAL COMMUNICATION",
        "HITECH CONSULTANCY",
        "SAS ENTERPRISES",
        "KK NAGAR",
        "SIA Hubify",
        "SHREE CHAKRA AGENCY",
        "SKM COMMUNICATION",
        "SMS COMMUNICATION",
        "SRI SAIRAM AENCY",
        "VENKAT SAI AGENCIES",
        "ATB Agency",
        "P&C",
        "Kalai Communication",
        "Rajas"
    ),
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
