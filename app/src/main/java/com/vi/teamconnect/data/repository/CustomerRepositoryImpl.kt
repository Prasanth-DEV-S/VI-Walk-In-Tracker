package com.vi.teamconnect.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.vi.teamconnect.data.local.StaticData
import com.vi.teamconnect.data.mapper.toDomain
import com.vi.teamconnect.data.mapper.toDto
import com.vi.teamconnect.data.model.CustomerVisitDto
import com.vi.teamconnect.domain.model.AuditLog
import com.vi.teamconnect.domain.model.BranchPerformance
import com.vi.teamconnect.domain.model.CustomerFilter
import com.vi.teamconnect.domain.model.CustomerVisit
import com.vi.teamconnect.domain.model.CustomerVisitHistory
import com.vi.teamconnect.domain.model.DashboardStats
import com.vi.teamconnect.domain.model.DateRangeOption
import com.vi.teamconnect.domain.model.EmployeePerformance
import com.vi.teamconnect.domain.model.SortBy
import com.vi.teamconnect.domain.repository.CustomerRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CustomerRepository {

    override fun getCustomerVisits(filter: CustomerFilter): Flow<List<CustomerVisit>> = callbackFlow {
        val collection = firestore.collection("customer_visits")

        val listener = collection.whereEqualTo("isDeleted", false)
            .addSnapshotListener { snapshot, _ ->
                val dtos = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(CustomerVisitDto::class.java)
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                var domainList = (dtos.map { it.toDomain() } + StaticData.staticCustomerVisits).distinctBy { it.recordId }

                // Apply Search Filter
                if (filter.searchQuery.isNotBlank()) {
                    val q = filter.searchQuery.trim().lowercase()
                    domainList = domainList.filter { visit ->
                        visit.customerName.lowercase().contains(q) ||
                        visit.mobileNumber.lowercase().contains(q) ||
                        visit.creName.lowercase().contains(q) ||
                        visit.storeName.lowercase().contains(q) ||
                        visit.branchName.lowercase().contains(q)
                    }
                }

                // Apply Branch Filter
                if (filter.branchId.isNotBlank()) {
                    domainList = domainList.filter { it.branchId == filter.branchId }
                }

                // Apply Employee Filter
                if (filter.employeeUid.isNotBlank()) {
                    domainList = domainList.filter { it.creUid == filter.employeeUid }
                }

                // Apply Store Filter
                if (filter.storeId.isNotBlank()) {
                    domainList = domainList.filter { it.storeId == filter.storeId }
                }

                // Apply PRE/POST Filter
                if (filter.prePost.isNotBlank()) {
                    domainList = domainList.filter { it.prePost.equals(filter.prePost, ignoreCase = true) }
                } else if (filter.prePp.isNotBlank()) {
                    val p = if (filter.prePp.equals("PP", ignoreCase = true)) "POSTPAID" else "PREPAID"
                    domainList = domainList.filter { it.prePost.equals(p, ignoreCase = true) }
                }

                // Apply GMB Filter
                if (filter.gmb.isNotBlank()) {
                    domainList = domainList.filter { it.gmb.equals(filter.gmb, ignoreCase = true) }
                }

                // Apply Tagging Filter
                if (filter.tagging.isNotBlank()) {
                    domainList = domainList.filter { it.tagging.equals(filter.tagging, ignoreCase = true) }
                }

                // Calculate date range bounds
                val (start, end) = calculateDateBounds(filter)
                if (start.isNotBlank()) {
                    domainList = domainList.filter { it.date >= start }
                }
                if (end.isNotBlank()) {
                    domainList = domainList.filter { it.date <= end }
                }

                // Apply CRE Name Filter if set
                if (filter.creName.isNotBlank()) {
                    domainList = domainList.filter { it.creName.equals(filter.creName, ignoreCase = true) }
                }

                // Apply Sorting
                domainList = when (filter.sortBy) {
                    SortBy.LATEST_DATE -> domainList.sortedWith(compareByDescending<CustomerVisit> { it.date }.thenByDescending { it.createdAt })
                    SortBy.OLDEST_DATE -> domainList.sortedWith(compareBy<CustomerVisit> { it.date }.thenBy { it.createdAt })
                    SortBy.CUSTOMER_NAME -> domainList.sortedBy { it.customerName.lowercase() }
                    SortBy.CRE_NAME -> domainList.sortedBy { it.creName.lowercase() }
                }

                trySend(domainList)
            }

        awaitClose { listener.remove() }
    }

    override fun getCustomerVisitById(recordId: String): Flow<CustomerVisit?> = callbackFlow {
        val listener = firestore.collection("customer_visits")
            .document(recordId)
            .addSnapshotListener { snapshot, _ ->
                val dto = try {
                    snapshot?.toObject(CustomerVisitDto::class.java)
                } catch (e: Exception) {
                    null
                }
                val domain = dto?.toDomain() ?: StaticData.staticCustomerVisits.find { it.recordId == recordId }
                trySend(domain)
            }
        awaitClose { listener.remove() }
    }

    override fun getDashboardStats(branchId: String?, dateRange: CustomerFilter?): Flow<DashboardStats> = callbackFlow {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val listener = firestore.collection("customer_visits")
            .whereEqualTo("isDeleted", false)
            .addSnapshotListener { snapshot, _ ->
                var visits = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(CustomerVisitDto::class.java)?.toDomain()
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                visits = (visits + StaticData.staticCustomerVisits).distinctBy { it.recordId }

                if (!branchId.isNullOrBlank()) {
                    visits = visits.filter { it.branchId == branchId }
                }

                if (dateRange != null) {
                    val (start, end) = calculateDateBounds(dateRange)
                    if (start.isNotBlank()) visits = visits.filter { it.date >= start }
                    if (end.isNotBlank()) visits = visits.filter { it.date <= end }
                }

                val totalVisits = visits.size
                val todaysVisits = visits.count { it.date == todayStr }
                val uniqueBranches = visits.map { it.branchId }.filter { it.isNotBlank() }.distinct().size
                val uniqueEmployees = visits.map { it.creUid }.filter { it.isNotBlank() }.distinct().size
                val prepaid = visits.count { it.prePost.equals("PREPAID", ignoreCase = true) }
                val postpaid = visits.count { it.prePost.equals("POSTPAID", ignoreCase = true) }
                val gmbYes = visits.count { it.gmb.equals("YES", ignoreCase = true) }
                val taggingYes = visits.count { it.tagging.equals("YES", ignoreCase = true) }

                trySend(
                    DashboardStats(
                        totalCustomerVisits = totalVisits,
                        todaysCustomerVisits = todaysVisits,
                        totalBranches = uniqueBranches,
                        totalEmployees = uniqueEmployees,
                        prepaidCount = prepaid,
                        postpaidCount = postpaid,
                        gmbYesCount = gmbYes,
                        taggingYesCount = taggingYes
                    )
                )
            }

        awaitClose { listener.remove() }
    }

    override fun getBranchPerformances(startDate: String, endDate: String): Flow<List<BranchPerformance>> = callbackFlow {
        val listener = firestore.collection("customer_visits")
            .whereEqualTo("isDeleted", false)
            .addSnapshotListener { snapshot, _ ->
                var visits = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(CustomerVisitDto::class.java)?.toDomain()
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                visits = (visits + StaticData.staticCustomerVisits).distinctBy { it.recordId }

                if (startDate.isNotBlank()) visits = visits.filter { it.date >= startDate }
                if (endDate.isNotBlank()) visits = visits.filter { it.date <= endDate }

                val branchGroups = visits.groupBy { it.branchId }
                val result = branchGroups.map { (bId, bVisits) ->
                    val bName = bVisits.firstOrNull()?.branchName ?: bId
                    BranchPerformance(
                        branchId = bId,
                        branchName = bName,
                        totalVisits = bVisits.size,
                        prepaidCount = bVisits.count { it.prePost.equals("PREPAID", ignoreCase = true) },
                        postpaidCount = bVisits.count { it.prePost.equals("POSTPAID", ignoreCase = true) },
                        gmbYesCount = bVisits.count { it.gmb.equals("YES", ignoreCase = true) },
                        taggingYesCount = bVisits.count { it.tagging.equals("YES", ignoreCase = true) }
                    )
                }

                trySend(result)
            }

        awaitClose { listener.remove() }
    }

    override fun getEmployeePerformances(
        branchId: String,
        startDate: String,
        endDate: String
    ): Flow<List<EmployeePerformance>> = callbackFlow {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val listener = firestore.collection("customer_visits")
            .whereEqualTo("isDeleted", false)
            .addSnapshotListener { snapshot, _ ->
                var visits = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(CustomerVisitDto::class.java)?.toDomain()
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()

                visits = (visits + StaticData.staticCustomerVisits).distinctBy { it.recordId }

                if (branchId.isNotBlank()) visits = visits.filter { it.branchId == branchId }
                if (startDate.isNotBlank()) visits = visits.filter { it.date >= startDate }
                if (endDate.isNotBlank()) visits = visits.filter { it.date <= endDate }

                val empGroups = visits.groupBy { it.creUid }
                val result = empGroups.map { (empUid, eVisits) ->
                    val empName = eVisits.firstOrNull()?.creName ?: empUid
                    val bName = eVisits.firstOrNull()?.branchName ?: ""
                    EmployeePerformance(
                        employeeUid = empUid,
                        employeeName = empName,
                        employeeId = empUid,
                        branchName = bName,
                        totalEntries = eVisits.size,
                        todaysEntries = eVisits.count { it.date == todayStr },
                        prepaidCount = eVisits.count { it.prePost.equals("PREPAID", ignoreCase = true) },
                        postpaidCount = eVisits.count { it.prePost.equals("POSTPAID", ignoreCase = true) },
                        gmbYesCount = eVisits.count { it.gmb.equals("YES", ignoreCase = true) },
                        taggingYesCount = eVisits.count { it.tagging.equals("YES", ignoreCase = true) }
                    )
                }

                trySend(result)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun addCustomerVisit(visit: CustomerVisit): Result<String> {
        return try {
            val recordId = if (visit.recordId.isNotBlank()) visit.recordId else UUID.randomUUID().toString()

            val finalVisit = visit.copy(
                recordId = recordId,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            firestore.collection("customer_visits")
                .document(recordId)
                .set(finalVisit.toDto())
                .await()

            // Audit log
            val logId = UUID.randomUUID().toString()
            val log = AuditLog(
                logId = logId,
                action = "CREATE",
                recordId = recordId,
                employeeUid = visit.createdBy,
                branchId = visit.branchId,
                changedFields = mapOf("action" to "Customer Record Created"),
                createdAt = System.currentTimeMillis()
            )

            firestore.collection("audit_logs")
                .document(logId)
                .set(log)
                .await()

            Result.success(recordId)
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Failed to add customer visit."))
        }
    }

    override suspend fun updateCustomerVisit(visit: CustomerVisit): Result<Unit> {
        return try {
            val updatedVisit = visit.copy(updatedAt = System.currentTimeMillis())

            firestore.collection("customer_visits")
                .document(visit.recordId)
                .set(updatedVisit.toDto())
                .await()

            val logId = UUID.randomUUID().toString()
            val log = AuditLog(
                logId = logId,
                action = "UPDATE",
                recordId = visit.recordId,
                employeeUid = visit.updatedBy,
                branchId = visit.branchId,
                changedFields = mapOf("action" to "Customer Record Updated"),
                createdAt = System.currentTimeMillis()
            )

            firestore.collection("audit_logs")
                .document(logId)
                .set(log)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Failed to update customer visit."))
        }
    }

    override suspend fun deleteCustomerVisit(recordId: String): Result<Unit> {
        return try {
            firestore.collection("customer_visits")
                .document(recordId)
                .update(
                    mapOf(
                        "isDeleted" to true,
                        "updatedAt" to System.currentTimeMillis()
                    )
                ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Failed to delete customer record."))
        }
    }

    override suspend fun recoverCustomerVisit(recordId: String): Result<Unit> {
        return try {
            firestore.collection("customer_visits")
                .document(recordId)
                .update(
                    mapOf(
                        "isDeleted" to false,
                        "updatedAt" to System.currentTimeMillis()
                    )
                ).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(e.localizedMessage ?: "Failed to recover customer record."))
        }
    }

    override fun getVisitHistory(recordId: String): Flow<List<CustomerVisitHistory>> = callbackFlow {
        val listener = firestore.collection("customer_visit_history")
            .whereEqualTo("recordId", recordId)
            .orderBy("changedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(CustomerVisitHistory::class.java)
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun generateToken(): String {
        val datePart = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val randomDigits = (1000..9999).random()
        return "TOK-$datePart-$randomDigits"
    }

    private fun calculateDateBounds(filter: CustomerFilter): Pair<String, String> {
        if (filter.startDate.isNotBlank() || filter.endDate.isNotBlank()) {
            return Pair(filter.startDate, filter.endDate)
        }

        val cal = Calendar.getInstance()
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)

        return when (filter.dateRange) {
            DateRangeOption.TODAY -> Pair(todayStr, todayStr)
            DateRangeOption.YESTERDAY -> {
                cal.add(Calendar.DAY_OF_YEAR, -1)
                val yestStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
                Pair(yestStr, yestStr)
            }
            DateRangeOption.LAST_7_DAYS -> {
                cal.add(Calendar.DAY_OF_YEAR, -6)
                val sevenDaysAgo = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
                Pair(sevenDaysAgo, todayStr)
            }
            DateRangeOption.LAST_30_DAYS -> {
                cal.add(Calendar.DAY_OF_YEAR, -29)
                val thirtyDaysAgo = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
                Pair(thirtyDaysAgo, todayStr)
            }
            else -> Pair("", "")
        }
    }
}
