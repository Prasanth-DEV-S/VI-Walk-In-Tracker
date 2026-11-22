package com.vi.teamconnect.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.vi.teamconnect.domain.model.CustomerVisit
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExportUtils {

    private val HEADERS = listOf(
        "STORE NAME",
        "BRANCH NAME",
        "DATE",
        "CRE NAME",
        "CUSTOMER NAME",
        "MOB NUMBER",
        "PRE/POST",
        "ALT NUMBER",
        "REASON FOR VISIT",
        "GMB",
        "TAGGING"
    )

    fun generateCsvContent(visits: List<CustomerVisit>): String {
        val sb = StringBuilder()
        sb.append(HEADERS.joinToString(",")).append("\n")

        for (visit in visits) {
            val row = listOf(
                escapeCsv(visit.storeName),
                escapeCsv(visit.branchName),
                escapeCsv(visit.date),
                escapeCsv(visit.creName),
                escapeCsv(visit.customerName),
                escapeCsv(visit.mobileNumber),
                escapeCsv(visit.prePost),
                escapeCsv(visit.altNumber),
                escapeCsv(visit.reasonForVisit),
                escapeCsv(visit.gmb),
                escapeCsv(visit.tagging)
            )
            sb.append(row.joinToString(",")).append("\n")
        }

        return sb.toString()
    }

    private fun escapeCsv(text: String): String {
        val clean = text.replace("\"", "\"\"").replace("\n", " ")
        return if (clean.contains(",") || clean.contains("\"") || clean.contains("\n")) {
            "\"$clean\""
        } else {
            clean
        }
    }

    fun shareCsvFile(context: Context, visits: List<CustomerVisit>, fileNamePrefix: String = "VI_Customer_Visits"): File? {
        return try {
            val csvText = generateCsvContent(visits)
            val dateStr = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "${fileNamePrefix}_$dateStr.csv"

            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val file = File(exportDir, fileName)
            file.writeText(csvText)

            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_SUBJECT, "VI Team Connect - Customer Visit Register Export")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(intent, "Share Customer Register"))
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
