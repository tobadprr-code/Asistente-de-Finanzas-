package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.FinancialSummary
import com.example.data.model.Transaction
import com.example.data.model.TransactionType
import java.io.File
import java.io.FileOutputStream

object Exporter {

    fun exportPdf(
        context: Context,
        summary: FinancialSummary,
        transactions: List<Transaction>
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 page size in points
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 22f
            isFakeBoldText = true
        }

        val subtitlePaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 12f
        }

        val headerPaint = Paint().apply {
            color = Color.parseColor("#1E293B")
            textSize = 12f
            isFakeBoldText = true
        }

        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 10f
        }

        val incomePaint = Paint().apply {
            color = Color.parseColor("#10B981")
            textSize = 10f
            isFakeBoldText = true
        }

        val expensePaint = Paint().apply {
            color = Color.parseColor("#F43F5E")
            textSize = 10f
            isFakeBoldText = true
        }

        var y = 40f

        // Title
        canvas.drawText("VALOR - REPORTE FINANCIERO", 40f, y, titlePaint)
        y += 20f
        canvas.drawText("Generado el: ${CurrencyFormatter.formatDate(System.currentTimeMillis())}", 40f, y, subtitlePaint)
        y += 30f

        // Summary Card Box
        val boxPaint = Paint().apply {
            color = Color.parseColor("#F8FAFC")
            style = Paint.Style.FILL
        }
        canvas.drawRect(40f, y, 555f, y + 70f, boxPaint)

        y += 25f
        canvas.drawText("Saldo Disponible: ${CurrencyFormatter.format(summary.totalBalance)}", 50f, y, headerPaint)
        canvas.drawText("Ingresos: ${CurrencyFormatter.format(summary.monthlyIncome)}", 250f, y, incomePaint)
        canvas.drawText("Egresos: ${CurrencyFormatter.format(summary.monthlyExpense)}", 410f, y, expensePaint)

        y += 60f

        // Table Header
        canvas.drawText("Fecha", 40f, y, headerPaint)
        canvas.drawText("Título", 120f, y, headerPaint)
        canvas.drawText("Categoría", 270f, y, headerPaint)
        canvas.drawText("Método", 390f, y, headerPaint)
        canvas.drawText("Monto", 480f, y, headerPaint)

        y += 10f
        val linePaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }
        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 20f

        // Table Rows
        for (tx in transactions.take(30)) {
            val dateStr = CurrencyFormatter.formatDateShort(tx.dateMillis)
            val titleStr = if (tx.title.length > 22) tx.title.take(20) + ".." else tx.title
            val categoryStr = tx.category
            val methodStr = tx.paymentMethod
            val amountStr = CurrencyFormatter.format(tx.amount)

            canvas.drawText(dateStr, 40f, y, textPaint)
            canvas.drawText(titleStr, 120f, y, textPaint)
            canvas.drawText(categoryStr, 270f, y, textPaint)
            canvas.drawText(methodStr, 390f, y, textPaint)

            val p = if (tx.type == TransactionType.INCOME) incomePaint else expensePaint
            val prefix = if (tx.type == TransactionType.INCOME) "+" else "-"
            canvas.drawText("$prefix$amountStr", 480f, y, p)

            y += 20f
            if (y > 800f) break
        }

        pdfDocument.finishPage(page)

        val file = File(context.cacheDir, "reporte_financiero_valor.pdf")
        return try {
            val os = FileOutputStream(file)
            pdfDocument.writeTo(os)
            os.close()
            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun exportCsv(context: Context, transactions: List<Transaction>): File? {
        val file = File(context.cacheDir, "movimientos_valor.csv")
        return try {
            val sb = StringBuilder()
            sb.append("ID,Fecha,Titulo,Tipo,Monto,Categoria,Metodo,Notas\n")
            for (tx in transactions) {
                sb.append("${tx.id},")
                sb.append("\"${CurrencyFormatter.formatDate(tx.dateMillis)}\",")
                sb.append("\"${tx.title.replace("\"", "\"\"")}\",")
                sb.append("${tx.type},")
                sb.append("${tx.amount},")
                sb.append("\"${tx.category}\",")
                sb.append("\"${tx.paymentMethod}\",")
                sb.append("\"${tx.notes.replace("\"", "\"\"")}\"\n")
            }
            file.writeText(sb.toString())
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareFile(context: Context, file: File, mimeType: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Compartir Reporte"))
    }
}
