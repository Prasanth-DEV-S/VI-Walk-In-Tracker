package com.vi.teamconnect.data.mapper

import com.vi.teamconnect.data.model.BranchDto
import com.vi.teamconnect.data.model.CustomerVisitDto
import com.vi.teamconnect.data.model.EmployeeDto
import com.vi.teamconnect.data.model.StoreDto
import com.vi.teamconnect.domain.model.AccountStatus
import com.vi.teamconnect.domain.model.Branch
import com.vi.teamconnect.domain.model.CustomerVisit
import com.vi.teamconnect.domain.model.Employee
import com.vi.teamconnect.domain.model.Role
import com.vi.teamconnect.domain.model.Store

fun parseTimestamp(value: Any?): Long {
    return try {
        when (value) {
            is com.google.firebase.Timestamp -> value.toDate().time
            is Long -> value
            is Double -> value.toLong()
            is Number -> value.toLong()
            is String -> value.toLongOrNull() ?: System.currentTimeMillis()
            else -> System.currentTimeMillis()
        }
    } catch (e: Throwable) {
        System.currentTimeMillis()
    }
}

fun parseNullableTimestamp(value: Any?): Long? {
    return try {
        when (value) {
            is com.google.firebase.Timestamp -> value.toDate().time
            is Long -> value
            is Double -> value.toLong()
            is Number -> value.toLong()
            is String -> value.toLongOrNull()
            else -> null
        }
    } catch (e: Throwable) {
        null
    }
}

fun EmployeeDto.toDomain(): Employee {
    val finalUid = if (!employeeUid.isNullOrBlank()) employeeUid else (uid ?: "")
    val finalName = if (!employeeName.isNullOrBlank()) employeeName else (fullName ?: "")
    val parsedRole = try {
        if (!role.isNullOrBlank()) Role.valueOf(role) else Role.EMPLOYEE
    } catch (e: Throwable) {
        Role.EMPLOYEE
    }
    val parsedStatus = try {
        if (!accountStatus.isNullOrBlank()) AccountStatus.valueOf(accountStatus) else AccountStatus.PENDING_APPROVAL
    } catch (e: Throwable) {
        AccountStatus.PENDING_APPROVAL
    }
    return Employee(
        employeeUid = finalUid,
        employeeId = employeeId ?: "",
        phoneNumber = phoneNumber ?: "",
        employeeName = finalName,
        branchId = branchId ?: "",
        branchName = branchName ?: "",
        role = parsedRole,
        accountStatus = parsedStatus,
        createdAt = parseTimestamp(createdAt),
        approvedAt = parseNullableTimestamp(approvedAt),
        approvedBy = approvedBy
    )
}

fun Employee.toDto(): EmployeeDto {
    val safeRole = try { role?.name ?: Role.EMPLOYEE.name } catch (e: Throwable) { Role.EMPLOYEE.name }
    val safeStatus = try { accountStatus?.name ?: AccountStatus.PENDING_APPROVAL.name } catch (e: Throwable) { AccountStatus.PENDING_APPROVAL.name }
    val safeUid = employeeUid ?: ""
    val safeName = employeeName ?: ""
    return EmployeeDto(
        employeeUid = safeUid,
        employeeId = employeeId ?: "",
        phoneNumber = phoneNumber ?: "",
        employeeName = safeName,
        branchId = branchId ?: "",
        branchName = branchName ?: "",
        role = safeRole,
        accountStatus = safeStatus,
        createdAt = createdAt,
        approvedAt = approvedAt,
        approvedBy = approvedBy,
        uid = safeUid,
        fullName = safeName,
        email = ""
    )
}

fun BranchDto.toDomain(): Branch {
    return Branch(
        branchId = branchId,
        branchName = branchName,
        branchCode = branchCode,
        address = address,
        managerUid = managerUid,
        isActive = isActive,
        createdAt = parseTimestamp(createdAt)
    )
}

fun Branch.toDto(): BranchDto {
    return BranchDto(
        branchId = branchId,
        branchName = branchName,
        branchCode = branchCode,
        address = address,
        managerUid = managerUid,
        isActive = isActive,
        createdAt = createdAt
    )
}

fun StoreDto.toDomain(): Store {
    return Store(
        storeId = storeId,
        storeName = storeName,
        branchId = branchId,
        branchName = branchName,
        isActive = isActive,
        createdAt = parseTimestamp(createdAt)
    )
}

fun Store.toDto(): StoreDto {
    return StoreDto(
        storeId = storeId,
        storeName = storeName,
        branchId = branchId,
        branchName = branchName,
        isActive = isActive,
        createdAt = createdAt
    )
}

fun CustomerVisitDto.toDomain(): CustomerVisit {
    val finalCusName = if (customerName.isNotBlank()) customerName else (cusName ?: "")
    val finalMob = if (mobileNumber.isNotBlank()) mobileNumber else (mobNo ?: "")
    val finalPrePost = if (prePost.isNotBlank()) {
        prePost
    } else if (prePp.equals("PP", ignoreCase = true) || prePp.equals("POSTPAID", ignoreCase = true)) {
        "POSTPAID"
    } else {
        "PREPAID"
    }
    val finalAlt = if (altNumber.isNotBlank()) altNumber else (alterNo ?: "")
    val finalReason = if (reasonForVisit.isNotBlank()) reasonForVisit else (purposeOfVisit ?: "")

    return CustomerVisit(
        recordId = recordId,
        storeId = storeId,
        storeName = storeName,
        branchId = branchId,
        branchName = branchName,
        date = date,
        creUid = creUid,
        creName = creName,
        customerName = finalCusName,
        mobileNumber = finalMob,
        prePost = finalPrePost,
        altNumber = finalAlt,
        reasonForVisit = finalReason,
        gmb = gmb,
        tagging = tagging,
        createdBy = createdBy,
        createdAt = parseTimestamp(createdAt),
        updatedBy = updatedBy,
        updatedAt = parseTimestamp(updatedAt),
        isDeleted = isDeleted
    )
}

fun CustomerVisit.toDto(): CustomerVisitDto {
    return CustomerVisitDto(
        recordId = recordId,
        storeId = storeId,
        storeName = storeName,
        branchId = branchId,
        branchName = branchName,
        date = date,
        creUid = creUid,
        creName = creName,
        customerName = customerName,
        mobileNumber = mobileNumber,
        prePost = prePost,
        altNumber = altNumber,
        reasonForVisit = reasonForVisit,
        gmb = gmb,
        tagging = tagging,
        createdBy = createdBy,
        createdAt = createdAt,
        updatedBy = updatedBy,
        updatedAt = updatedAt,
        isDeleted = isDeleted,
        cusName = customerName,
        mobNo = mobileNumber,
        prePp = if (prePost.equals("POSTPAID", ignoreCase = true)) "PP" else "PRE",
        alterNo = altNumber,
        purposeOfVisit = reasonForVisit
    )
}
