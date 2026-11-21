package com.vi.teamconnect.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vi.teamconnect.presentation.auth.ForgotPasswordScreen
import com.vi.teamconnect.presentation.auth.ForgotPasswordViewModel
import com.vi.teamconnect.presentation.auth.LoginScreen
import com.vi.teamconnect.presentation.auth.LoginViewModel
import com.vi.teamconnect.presentation.auth.RegisterScreen
import com.vi.teamconnect.presentation.auth.RegisterViewModel
import com.vi.teamconnect.presentation.customer.AddEditCustomerScreen
import com.vi.teamconnect.presentation.customer.AddEditCustomerViewModel
import com.vi.teamconnect.presentation.customer.CustomerDetailScreen
import com.vi.teamconnect.presentation.customer.CustomerDetailViewModel
import com.vi.teamconnect.presentation.customer.CustomerListScreen
import com.vi.teamconnect.presentation.customer.CustomerListViewModel
import com.vi.teamconnect.presentation.dashboard.HomeScreen
import com.vi.teamconnect.presentation.dashboard.HomeViewModel
import com.vi.teamconnect.presentation.employee.EmployeeManagementScreen
import com.vi.teamconnect.presentation.employee.EmployeeManagementViewModel
import com.vi.teamconnect.presentation.notifications.NotificationsScreen
import com.vi.teamconnect.presentation.notifications.NotificationsViewModel
import com.vi.teamconnect.presentation.profile.ProfileScreen
import com.vi.teamconnect.presentation.profile.ProfileViewModel
import com.vi.teamconnect.presentation.reports.ReportsScreen
import com.vi.teamconnect.presentation.reports.ReportsViewModel
import com.vi.teamconnect.presentation.settings.SettingsScreen
import com.vi.teamconnect.presentation.settings.SettingsViewModel

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Screen.Home.route, Icons.Default.Home),
    BottomNavItem("Records", Screen.CustomerList.route, Icons.AutoMirrored.Filled.List),
    BottomNavItem("Reports", Screen.Reports.route, Icons.Default.Assessment),
    BottomNavItem("Team", Screen.EmployeeManagement.route, Icons.Default.People),
    BottomNavItem("Profile", Screen.Profile.route, Icons.Default.Person)
)

@Composable
fun MainAppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Auth Routes
            composable(Screen.Login.route) {
                val vm: LoginViewModel = hiltViewModel()
                LoginScreen(
                    viewModel = vm,
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
                )
            }

            composable(Screen.Register.route) {
                val vm: RegisterViewModel = hiltViewModel()
                RegisterScreen(
                    viewModel = vm,
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }

            composable(Screen.ForgotPassword.route) {
                val vm: ForgotPasswordViewModel = hiltViewModel()
                ForgotPasswordScreen(
                    viewModel = vm,
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }

            // Main Routes
            composable(Screen.Home.route) {
                val vm: HomeViewModel = hiltViewModel()
                HomeScreen(
                    viewModel = vm,
                    onNavigateToAddCustomer = { navController.navigate(Screen.AddEditCustomer.createRoute("new")) },
                    onNavigateToCustomerDetail = { id -> navController.navigate(Screen.CustomerDetail.createRoute(id)) }
                )
            }

            composable(Screen.CustomerList.route) {
                val vm: CustomerListViewModel = hiltViewModel()
                CustomerListScreen(
                    viewModel = vm,
                    onNavigateToAddCustomer = { navController.navigate(Screen.AddEditCustomer.createRoute("new")) },
                    onNavigateToCustomerDetail = { id -> navController.navigate(Screen.CustomerDetail.createRoute(id)) }
                )
            }

            composable(
                route = Screen.AddEditCustomer.route,
                arguments = listOf(navArgument("recordId") { type = NavType.StringType; defaultValue = "new" })
            ) {
                val vm: AddEditCustomerViewModel = hiltViewModel()
                AddEditCustomerScreen(
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.CustomerDetail.route,
                arguments = listOf(navArgument("recordId") { type = NavType.StringType })
            ) {
                val vm: CustomerDetailViewModel = hiltViewModel()
                CustomerDetailScreen(
                    viewModel = vm,
                    onNavigateToEdit = { id -> navController.navigate(Screen.AddEditCustomer.createRoute(id)) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Reports.route) {
                val vm: ReportsViewModel = hiltViewModel()
                ReportsScreen(
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.EmployeeManagement.route) {
                val vm: EmployeeManagementViewModel = hiltViewModel()
                EmployeeManagementScreen(
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Notifications.route) {
                val vm: NotificationsViewModel = hiltViewModel()
                NotificationsScreen(
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                val vm: ProfileViewModel = hiltViewModel()
                ProfileScreen(
                    viewModel = vm,
                    onLogoutSuccess = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Settings.route) {
                val vm: SettingsViewModel = hiltViewModel()
                SettingsScreen(
                    viewModel = vm,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
