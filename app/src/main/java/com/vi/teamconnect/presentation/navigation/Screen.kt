package com.vi.teamconnect.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object Home : Screen("home")
    object CustomerList : Screen("customer_list")
    object AddEditCustomer : Screen("add_edit_customer/{recordId}") {
        fun createRoute(recordId: String = "new") = "add_edit_customer/$recordId"
    }
    object CustomerDetail : Screen("customer_detail/{recordId}") {
        fun createRoute(recordId: String) = "customer_detail/$recordId"
    }
    object Reports : Screen("reports")
    object EmployeeManagement : Screen("employee_management")
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
}
