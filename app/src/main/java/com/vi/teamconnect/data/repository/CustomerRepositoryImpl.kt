package com.vi.teamconnect.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.vi.teamconnect.data.mapper.toDomain
import com.vi.teamconnect.data.mapper.toDto
import com.vi.teamconnect.data.model.CustomerVisitDto
import com.vi.teamconnect.domain.model.CustomerFilter
import com.vi.teamconnect.domain.model.CustomerVisit
import com.vi.teamconnect.domain.model.CustomerVisitHistory
import com.vi.teamconnect.domain.model.DashboardStats
import com.vi.teamconnect.domain.model.SortBy
import com.vi.teamconnect.domain.repository.CustomerRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
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
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val dtos = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(CustomerVisitDto::class.java)
                } ?: emptyList()

                var domainList = dtos.map { it.toDomain() }

                // Apply Search Filter
                if (filter.searchQuery.isNotBlank()) {
                    val q = filter.searchQuery.trim().lowercase()
                    domainList = domainList.filter { visit ->
                        visit.cusName.lowercase().contains(q) ||
                        visit.mobNo.lowercase().contains(q) ||
                        visit.token.lowercase().contains(q) ||
                        visit.srNo.lowercase().contains(q) ||
                        visit.creName.lowercase().contains(q)
                    }
                }

                // Apply Date Range Filter
                if (filter.startDate.isNotBlank()) {
                    domainList = domainList.filter { it.date >= filter.startDate }
                }
                if (filter.endDate.isNotBlank()) {
                    domainList = domainList.filter { it.date <= filter.endDate }
                }

                // Apply CRE Name Filter
                if (filter.creName.isNotBlank()) {
                    domainList = domainList.filter { it.creName.equals(filter.creName, ignoreCase = true) }
                }

                // Apply Purpose Filter
                if (filter.purposeOfVisit.isNotBlank()) {
                    domainList = domainList.filter { it.purposeOfVisit.equals(filter.purposeOfVisit, ignoreCase = true) }
                }

                // Apply PRE/PP Filter
                if (filter.prePp.isNotBlank()) {
                    domainList = domainList.filter { it.prePp.equals(filter.prePp, ignoreCase = true) }
                }

                // Apply Operator Filter
                if (filter.operator.isNotBlank()) {
                    domainList = domainList.filter { it.operator.equals(filter.operator, ignoreCase = true) }
                }

                // Apply Happy Calling Filter
                if (filter.happyCalling.isNotBlank()) {
                    domainList = domainList.filter { it.happyCalling.equals(filter.happyCalling, ignoreCase = true) }
                }

                // Apply Referral Filter
                if (filter.referral.isNotBlank()) {
                    domainList = domainList.filter { it.refferel.equals(filter.referral, ignoreCase = true) }
                }

                // Apply Sorting
                domainList = when (filter.sortBy) {
                    SortBy.LATEST_DATE -> domainList.sortedWith(compareByDescending<CustomerVisit> { it.date }.thenByDescending { it.createdAt })
                    SortBy.OLDEST_DATE -> domainList.sortedWith(compareBy<CustomerVisit> { it.date }.thenBy { it.createdAt })
                    SortBy.CUSTOMER_NAME -> domainList.sortedBy { it.cusName.lowercase() }
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
                val dto = snapshot?.toObject(CustomerVisitDto::class.java)
                trySend(dto?.toDomain())
            }
        awaitClose { listener.remove() }
    }

    override fun getDashboardStats(): Flow<DashboardStats> = callbackFlow {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentMonthPrefix = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

        val visitsListener = firestore.collection("customer_visits")
            .whereEqualTo("isDeleted", false)
            .addSnapshotListener { snapshot, _ ->
                val visits = snapshot?.documents?.mapNotNull { it.toObject(CustomerVisitDto::class.java)?.toDomain() } ?: emptyList()

                val totalRecords = visits.size
                val todaysVisits = visits.count { it.date == todayStr }
                val uniqueCres = visits.map { it.creName.trim() }.filter { it.isNotEmpty() }.distinct().size
                val happyCallingYes = visits.count { it.happyCalling.equals("Yes", ignoreCase = true) }
                val pendingFollowups = visits.count { it.happyCalling.equals("Pending", ignoreCase = true) }
                val monthlyRecords = visits.count { it.date.startsWith(currentMonthPrefix) }

                trySend(
                    DashboardStats(
                        totalCustomerRecords = totalRecords,
                        todaysVisits = todaysVisits,
                        totalCres = uniqueCres,
                        happyCallingYes = happyCallingYes,
                        pendingFollowups = pendingFollowups,
                        monthlyRecords = monthlyRecords
                    )
                )
            }

        awaitClose { visitsListener.remove() }
    }

    override suspend fun addCustomerVisit(visit: CustomerVisit): Result<String> {
        return try {
            val recordId = if (visit.recordId.isNotBlank()) visit.recordId else UUID.randomUUID().toString()
            val finalToken = if (visit.token.isNotBlank()) visit.token else generateToken()

            val finalVisit = visit.copy(
                recordId = recordId,
                token = finalToken,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            firestore.collection("customer_visits")
                .document(recordId)
                .set(finalVisit.toDto())
                .await()

            // Record History
            val historyId = UUID.randomUUID().toString()
            val history = CustomerVisitHistory(
                historyId = historyId,
                recordId = recordId,
                action = "CREATE",
                changedFields = mapOf("status" to "Record Created"),
                changedBy = visit.createdBy,
                changedAt = System.currentTimeMillis()
            )

            firestore.collection("customer_visit_history")
                .document(historyId)
                .set(history)
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

            val historyId = UUID.randomUUID().toString()
            val history = CustomerVisitHistory(
                historyId = historyId,
                recordId = visit.recordId,
                action = "UPDATE",
                changedFields = mapOf("status" to "Record Updated"),
                changedBy = visit.updatedBy,
                changedAt = System.currentTimeMillis()
            )

            firestore.collection("customer_visit_history")
                .document(historyId)
                .set(history)
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
                        "deletedAt" to System.currentTimeMillis()
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
                        "deletedAt" to null,
                        "deletedBy" to null
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
                    doc.toObject(CustomerVisitHistory::class.java)
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
}
