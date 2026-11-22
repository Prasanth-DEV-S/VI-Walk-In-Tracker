package com.vi.teamconnect.data.local

import com.vi.teamconnect.domain.model.AccountStatus
import com.vi.teamconnect.domain.model.CustomerVisit
import com.vi.teamconnect.domain.model.Employee
import com.vi.teamconnect.domain.model.Role
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Static Data Repository providing fallback & static Admin updates and static Employee dataset.
 */
object StaticData {

    // Default Static Admin User
    val staticAdmin = Employee(
        employeeUid = "ADMIN-UID-001",
        employeeId = "EMP-ADMIN001",
        employeeName = "System Admin",
        phoneNumber = "+91 9345540171",
        branchId = "BRANCH-HQ-01",
        branchName = "Central Headquarters",
        role = Role.ADMIN,
        accountStatus = AccountStatus.ACTIVE,
        createdAt = System.currentTimeMillis() - 864000000L,
        approvedAt = System.currentTimeMillis() - 864000000L,
        approvedBy = "SYSTEM"
    )

    // Initial Static Employees List (Admin, Managers, TLs, Active CREs, and Pending Employees)
    val staticEmployees = CopyOnWriteArrayList<Employee>(
        listOf(
            staticAdmin,
            Employee(
                employeeUid = "EMP-UID-1001",
                employeeId = "EMP-1001",
                employeeName = "Rahul Sharma",
                phoneNumber = "+91 9811122233",
                branchId = "BRANCH-DEL-01",
                branchName = "Connaught Place Branch",
                role = Role.EMPLOYEE,
                accountStatus = AccountStatus.ACTIVE,
                createdAt = System.currentTimeMillis() - 432000000L
            ),
            Employee(
                employeeUid = "EMP-UID-1002",
                employeeId = "EMP-1002",
                employeeName = "Priya Patel",
                phoneNumber = "+91 9822233344",
                branchId = "BRANCH-MUM-01",
                branchName = "Andheri West Branch",
                role = Role.EMPLOYEE,
                accountStatus = AccountStatus.ACTIVE,
                createdAt = System.currentTimeMillis() - 345600000L
            ),
            Employee(
                employeeUid = "EMP-UID-1003",
                employeeId = "EMP-1003",
                employeeName = "Ananya Verma",
                phoneNumber = "+91 9833344455",
                branchId = "BRANCH-MUM-01",
                branchName = "Andheri West Branch",
                role = Role.MANAGER,
                accountStatus = AccountStatus.ACTIVE,
                createdAt = System.currentTimeMillis() - 259200000L
            ),
            Employee(
                employeeUid = "EMP-UID-1004",
                employeeId = "EMP-1004",
                employeeName = "Amit Kumar",
                phoneNumber = "+91 9844455566",
                branchId = "BRANCH-BLR-01",
                branchName = "Koramangala Branch",
                role = Role.EMPLOYEE,
                accountStatus = AccountStatus.ACTIVE,
                createdAt = System.currentTimeMillis() - 172800000L
            ),
            Employee(
                employeeUid = "EMP-UID-1005",
                employeeId = "EMP-1005",
                employeeName = "Suresh Raina",
                phoneNumber = "+91 9855566677",
                branchId = "BRANCH-DEL-01",
                branchName = "Connaught Place Branch",
                role = Role.EMPLOYEE,
                accountStatus = AccountStatus.PENDING_APPROVAL,
                createdAt = System.currentTimeMillis() - 86400000L
            ),
            Employee(
                employeeUid = "EMP-UID-1006",
                employeeId = "EMP-1006",
                employeeName = "Kavita Das",
                phoneNumber = "+91 9866677788",
                branchId = "BRANCH-BLR-01",
                branchName = "Koramangala Branch",
                role = Role.EMPLOYEE,
                accountStatus = AccountStatus.PENDING_APPROVAL,
                createdAt = System.currentTimeMillis() - 43200000L
            )
        )
    )

    // Initial Static Customer Visit Records
    val staticCustomerVisits = CopyOnWriteArrayList<CustomerVisit>(
        listOf(
            CustomerVisit(
                recordId = "REC-1001",
                storeId = "STORE-CP-01",
                storeName = "Vi Store CP",
                branchId = "BRANCH-DEL-01",
                branchName = "Connaught Place Branch",
                date = "2026-02-15",
                creUid = "EMP-UID-1001",
                creName = "Rahul Sharma",
                customerName = "Vikram Malhotra",
                mobileNumber = "9876543210",
                prePost = "PREPAID",
                altNumber = "9876543211",
                reasonForVisit = "SIM Replacement",
                gmb = "YES",
                tagging = "YES",
                createdBy = "EMP-UID-1001",
                createdAt = System.currentTimeMillis() - 86400000L
            ),
            CustomerVisit(
                recordId = "REC-1002",
                storeId = "STORE-ANDHERI-01",
                storeName = "Vi Store Andheri",
                branchId = "BRANCH-MUM-01",
                branchName = "Andheri West Branch",
                date = "2026-02-15",
                creUid = "EMP-UID-1003",
                creName = "Ananya Verma",
                customerName = "Sneha Kulkarni",
                mobileNumber = "9812345678",
                prePost = "POSTPAID",
                altNumber = "9812345679",
                reasonForVisit = "Bill Payment & Plan Upgrade",
                gmb = "YES",
                tagging = "NO",
                createdBy = "EMP-UID-1003",
                createdAt = System.currentTimeMillis() - 43200000L
            )
        )
    )

    // Static Admin Credentials Lookup
    private val staticCredentialsMap: Map<String, Employee>
        get() {
            val map = mutableMapOf(
                "admin@vi.com" to staticAdmin,
                "admin@teamconnect.com" to staticAdmin,
                "admin" to staticAdmin
            )
            staticEmployees.getOrNull(1)?.let {
                map["emp1@vi.com"] = it
                map["rahul.sharma@vi.com"] = it
            }
            staticEmployees.getOrNull(2)?.let {
                map["emp2@vi.com"] = it
                map["priya.patel@vi.com"] = it
            }
            staticEmployees.getOrNull(3)?.let {
                map["mgr1@vi.com"] = it
                map["ananya.verma@vi.com"] = it
            }
            staticEmployees.getOrNull(4)?.let {
                map["emp4@vi.com"] = it
                map["amit.kumar@vi.com"] = it
            }
            return map
        }

    /**
     * Finds a static employee matching an email or employee ID
     */
    fun findStaticEmployee(query: String): Employee? {
        val q = query.trim().lowercase()
        return staticCredentialsMap[q] ?: staticEmployees.find {
            it.employeeId.lowercase() == q ||
            it.employeeName.lowercase() == q ||
            it.employeeUid.lowercase() == q
        }
    }

    /**
     * Admin Static Update: Approve pending employee
     */
    fun approveEmployee(uid: String, adminName: String): Boolean {
        val index = staticEmployees.indexOfFirst { it.employeeUid == uid }
        if (index != -1) {
            val emp = staticEmployees[index]
            staticEmployees[index] = emp.copy(
                accountStatus = AccountStatus.ACTIVE,
                approvedAt = System.currentTimeMillis(),
                approvedBy = adminName
            )
            return true
        }
        return false
    }

    /**
     * Admin Static Update: Reject pending employee
     */
    fun rejectEmployee(uid: String): Boolean {
        val index = staticEmployees.indexOfFirst { it.employeeUid == uid }
        if (index != -1) {
            val emp = staticEmployees[index]
            staticEmployees[index] = emp.copy(accountStatus = AccountStatus.REJECTED)
            return true
        }
        return false
    }

    /**
     * Admin Static Update: Change employee role (EMPLOYEE, TL, MANAGER, ADMIN)
     */
    fun updateEmployeeRole(uid: String, newRole: Role): Boolean {
        val index = staticEmployees.indexOfFirst { it.employeeUid == uid }
        if (index != -1) {
            val emp = staticEmployees[index]
            staticEmployees[index] = emp.copy(role = newRole)
            return true
        }
        return false
    }

    /**
     * Admin Static Update: Change employee status (ACTIVE, PENDING_APPROVAL, REJECTED, SUSPENDED)
     */
    fun updateEmployeeStatus(uid: String, newStatus: AccountStatus): Boolean {
        val index = staticEmployees.indexOfFirst { it.employeeUid == uid }
        if (index != -1) {
            val emp = staticEmployees[index]
            staticEmployees[index] = emp.copy(accountStatus = newStatus)
            return true
        }
        return false
    }

    /**
     * Static Update: Update employee profile info
     */
    fun updateEmployeeProfile(updated: Employee): Boolean {
        val index = staticEmployees.indexOfFirst { it.employeeUid == updated.employeeUid }
        if (index != -1) {
            staticEmployees[index] = updated
            return true
        } else {
            staticEmployees.add(updated)
            return true
        }
    }

    /**
     * Static Register: Adds new employee registration
     */
    fun addEmployee(employee: Employee) {
        if (staticEmployees.none { it.employeeUid == employee.employeeUid }) {
            staticEmployees.add(employee)
        }
    }
}
