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
        "DATE",
        "TOKEN",
        "CRE NAME",
        "CUS NAME",
        "MOB NO",
        "PURPOSE OF VISIT",
        "PRE/PP",
        "ALTER NO",
        "SR NO",
        "OPERATOR",
        "GBP",
        "Remarks",
        "Happy calling",
        "DID",
        "Refferel"
    )

    fun generateCsvContent(visits: List<CustomerVisit>): String {
        val sb = StringBuilder()
        sb.append(HEADERS.joinToString(",")).append("\n")

        for (visit in visits) {
            val row = listOf(
                escapeCsv(visit.date),
                escapeCsv(visit.token),
                escapeCsv(visit.creName),
                escapeCsv(visit.cusName),
                escapeCsv(visit.mobNo),
                escapeCsv(visit.purposeOfVisit),
                escapeCsv(visit.prePp),
                escapeCsv(visit.alterNo),
                escapeCsv(visit.srNo),
                escapeCsv(visit.operator),
                escapeCsv(visit.gbp),
                escapeCsv(visit.remarks),
                escapeCsv(visit.happyCalling),
                escapeCsv(visit.did),
                escapeCsv(visit.refferel)
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
