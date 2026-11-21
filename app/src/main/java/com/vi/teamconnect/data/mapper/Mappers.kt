package com.vi.teamconnect.data.mapper

import com.vi.teamconnect.data.model.CustomerVisitDto
import com.vi.teamconnect.data.model.EmployeeDto
import com.vi.teamconnect.domain.model.AccountStatus
import com.vi.teamconnect.domain.model.CustomerVisit
import com.vi.teamconnect.domain.model.Employee
import com.vi.teamconnect.domain.model.Role

fun EmployeeDto.toDomain(): Employee {
    return Employee(
        uid = uid,
        employeeId = employeeId,
        fullName = fullName,
        email = email,
        phoneNumber = phoneNumber,
        role = try { Role.valueOf(role) } catch (e: Exception) { Role.EMPLOYEE },
        accountStatus = try { AccountStatus.valueOf(accountStatus) } catch (e: Exception) { AccountStatus.PENDING_APPROVAL },
        createdAt = createdAt,
        approvedAt = approvedAt,
        approvedBy = approvedBy
    )
}

fun Employee.toDto(): EmployeeDto {
    return EmployeeDto(
        uid = uid,
        employeeId = employeeId,
        fullName = fullName,
        email = email,
        phoneNumber = phoneNumber,
        role = role.name,
        accountStatus = accountStatus.name,
        createdAt = createdAt,
        approvedAt = approvedAt,
        approvedBy = approvedBy
    )
}

fun CustomerVisitDto.toDomain(): CustomerVisit {
    return CustomerVisit(
        recordId = recordId,
        date = date,
        token = token,
        creName = creName,
        creEmployeeId = creEmployeeId,
        cusName = cusName,
        mobNo = mobNo,
        purposeOfVisit = purposeOfVisit,
        prePp = prePp,
        alterNo = alterNo,
        srNo = srNo,
        operator = operator,
        gbp = gbp,
        remarks = remarks,
        happyCalling = happyCalling,
        did = did,
        refferel = refferel,
        createdBy = createdBy,
        createdAt = createdAt,
        updatedBy = updatedBy,
        updatedAt = updatedAt,
        isDeleted = isDeleted,
        deletedBy = deletedBy,
        deletedAt = deletedAt
    )
}

fun CustomerVisit.toDto(): CustomerVisitDto {
    return CustomerVisitDto(
        recordId = recordId,
        date = date,
        token = token,
        creName = creName,
        creEmployeeId = creEmployeeId,
        cusName = cusName,
        mobNo = mobNo,
        purposeOfVisit = purposeOfVisit,
        prePp = prePp,
        alterNo = alterNo,
        srNo = srNo,
        operator = operator,
        gbp = gbp,
        remarks = remarks,
        happyCalling = happyCalling,
        did = did,
        refferel = refferel,
        createdBy = createdBy,
        createdAt = createdAt,
        updatedBy = updatedBy,
        updatedAt = updatedAt,
        isDeleted = isDeleted,
        deletedBy = deletedBy,
        deletedAt = deletedAt
    )
}
