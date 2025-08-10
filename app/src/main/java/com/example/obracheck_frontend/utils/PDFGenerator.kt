@file:Suppress("SpellCheckingInspection") // evita warning por palabras en español (p.ej., "Colores")

package com.example.obracheck_frontend.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.graphics.toColorInt
import com.example.obracheck_frontend.model.domain.Attendance
import com.example.obracheck_frontend.model.dto.AttendanceStatus
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Suppress("unused") // se usa desde la UI; silencia "Class is never used" si aún no lo llamas
class PDFGenerator {

    companion object {
        private const val PAGE_WIDTH = 595 // A4 width in points
        private const val PAGE_HEIGHT = 842 // A4 height in points
        private const val MARGIN = 40
        private const val HEADER_HEIGHT = 80

        // Colores ObraCheck
        private const val BRAND_COLOR = 0xFF1F2A33.toInt()
        private const val ACCENT_COLOR = 0xFFF6C445.toInt()
        private const val SUCCESS_COLOR = 0xFF10B981.toInt()
        private const val DANGER_COLOR = 0xFFEF4444.toInt()
        private const val MUTED_COLOR = 0xFF7B8AA0.toInt()

        fun generateAttendanceReport(
            context: Context,
            attendances: List<Attendance>,
            siteName: String,
            date: String,
            onSuccess: (File) -> Unit,
            onError: (String) -> Unit
        ) {
            try {
                val pdfDocument = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas

                // Paints
                val titlePaint = Paint().apply {
                    color = BRAND_COLOR
                    textSize = 24f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }

                val subtitlePaint = Paint().apply {
                    color = MUTED_COLOR
                    textSize = 16f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    isAntiAlias = true
                }

                val headerPaint = Paint().apply {
                    color = BRAND_COLOR
                    textSize = 14f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    isAntiAlias = true
                }

                val bodyPaint = Paint().apply {
                    color = BRAND_COLOR
                    textSize = 12f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    isAntiAlias = true
                }

                val accentPaint = Paint().apply {
                    color = ACCENT_COLOR
                    isAntiAlias = true
                }

                // Posición inicial
                var currentY = MARGIN.toFloat()

                // Header
                drawHeader(canvas, titlePaint, subtitlePaint, accentPaint, siteName, date, currentY)
                currentY += HEADER_HEIGHT + 20

                // Resumen
                currentY = drawSummary(canvas, attendances, headerPaint, bodyPaint, currentY)
                currentY += 30

                // Tabla (SIN FIRMA)
                drawAttendanceTable(canvas, attendances, headerPaint, bodyPaint, accentPaint, currentY)

                pdfDocument.finishPage(page)

                // Guardar en carpeta de Documentos de la app (no requiere permisos)
                val fileName = "reporte_asistencia_${date.replace("-", "_")}.pdf"
                val outDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                    ?: context.filesDir
                val file = File(outDir, fileName)

                FileOutputStream(file).use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
                pdfDocument.close()

                onSuccess(file)

            } catch (e: Exception) {
                e.printStackTrace()
                onError("Error al generar PDF: ${e.message ?: "desconocido"}")
            }
        }

        private fun drawHeader(
            canvas: Canvas,
            titlePaint: Paint,
            subtitlePaint: Paint,
            accentPaint: Paint,
            siteName: String,
            date: String,
            startY: Float
        ) {
            var y = startY + 20

            // Barra superior amarilla
            canvas.drawRect(
                MARGIN.toFloat(),
                startY,
                (PAGE_WIDTH - MARGIN).toFloat(),
                startY + 6,
                accentPaint
            )

            // Título
            canvas.drawText("REPORTE DE ASISTENCIA", MARGIN.toFloat(), y, titlePaint)
            y += 30

            // Info sitio y fecha
            val formattedDate = try {
                val localDate = LocalDate.parse(date)
                localDate.format(
                    DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
                )
            } catch (_: Exception) {
                date
            }

            canvas.drawText("Obra: $siteName", MARGIN.toFloat(), y, subtitlePaint)
            y += 20
            canvas.drawText("Fecha: $formattedDate", MARGIN.toFloat(), y, subtitlePaint)
        }

        private fun drawSummary(
            canvas: Canvas,
            attendances: List<Attendance>,
            headerPaint: Paint,
            bodyPaint: Paint,
            startY: Float
        ): Float {
            var y = startY

            // Título resumen
            canvas.drawText("RESUMEN", MARGIN.toFloat(), y, headerPaint)
            y += 25

            // Stats (4 columnas: Total, Presentes, Ausentes, Tardíos)
            val total = attendances.size
            val present = attendances.count { it.state == AttendanceStatus.PRESENT }
            val absent = attendances.count { it.state == AttendanceStatus.ABSENT }
            val late = attendances.count { it.state == AttendanceStatus.LATE }

            val columnWidth = (PAGE_WIDTH - 2 * MARGIN) / 4f

            var x = MARGIN.toFloat()
            canvas.drawText("Total", x, y, headerPaint); x += columnWidth
            canvas.drawText("Presentes", x, y, headerPaint); x += columnWidth
            canvas.drawText("Ausentes", x, y, headerPaint); x += columnWidth
            canvas.drawText("Tardíos", x, y, headerPaint)

            y += 20
            x = MARGIN.toFloat()
            canvas.drawText(total.toString(), x, y, bodyPaint); x += columnWidth
            canvas.drawText(present.toString(), x, y, bodyPaint); x += columnWidth
            canvas.drawText(absent.toString(), x, y, bodyPaint); x += columnWidth
            canvas.drawText(late.toString(), x, y, bodyPaint)

            return y
        }

        private fun drawAttendanceTable(
            canvas: Canvas,
            attendances: List<Attendance>,
            headerPaint: Paint,
            bodyPaint: Paint,
            accentPaint: Paint,
            startY: Float
        ) {
            var y = startY
            val rowHeight = 25f
            // 4 columnas (SIN FIRMA)
            val columnWidths = floatArrayOf(50f, 220f, 120f, 120f) // #, Nombre, CI, Estado

            // Título
            canvas.drawText("LISTA DE TRABAJADORES", MARGIN.toFloat(), y, headerPaint)
            y += 30

            // Encabezados
            var x = MARGIN.toFloat()
            val headerY = y
            canvas.drawText("#", x + 10, headerY, headerPaint); x += columnWidths[0]
            canvas.drawText("Nombre", x + 10, headerY, headerPaint); x += columnWidths[1]
            canvas.drawText("Cédula", x + 10, headerY, headerPaint); x += columnWidths[2]
            canvas.drawText("Estado", x + 10, headerY, headerPaint)

            // Línea bajo headers
            y += 5
            canvas.drawRect(
                MARGIN.toFloat(),
                y,
                MARGIN + columnWidths.sum(),
                y + 2,
                accentPaint
            )
            y += rowHeight

            // Filas
            val zebraBg = "#F9FAFB".toColorInt()
            val lateColor = "#F59E0B".toColorInt()

            attendances.takeWhile { y <= PAGE_HEIGHT - 100 }.forEachIndexed { index, attendance ->
                x = MARGIN.toFloat()
                val rowY = y

                // Fondo alterno
                if (index % 2 == 0) {
                    val bgPaint = Paint().apply { color = zebraBg }
                    canvas.drawRect(
                        MARGIN.toFloat(),
                        rowY - 15,
                        MARGIN + columnWidths.sum(),
                        rowY + 5,
                        bgPaint
                    )
                }

                // #
                canvas.drawText((index + 1).toString(), x + 10, rowY, bodyPaint)
                x += columnWidths[0]

                // Nombre
                val name = if (attendance.workerName.length > 28) {
                    attendance.workerName.take(25) + "..."
                } else attendance.workerName
                canvas.drawText(name, x + 10, rowY, bodyPaint)
                x += columnWidths[1]

                // CI
                canvas.drawText(attendance.ci, x + 10, rowY, bodyPaint)
                x += columnWidths[2]

                // Estado con color
                val statusPaint = Paint(bodyPaint).apply {
                    color = when (attendance.state) {
                        AttendanceStatus.PRESENT -> SUCCESS_COLOR
                        AttendanceStatus.ABSENT -> DANGER_COLOR
                        AttendanceStatus.LATE -> lateColor
                        AttendanceStatus.NA -> MUTED_COLOR
                    }
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
                val statusText = when (attendance.state) {
                    AttendanceStatus.PRESENT -> "✓ PRESENTE"
                    AttendanceStatus.ABSENT -> "✗ AUSENTE"
                    AttendanceStatus.LATE -> "⚠ TARDÍO"
                    AttendanceStatus.NA -> "- PENDIENTE"
                }
                canvas.drawText(statusText, x + 10, rowY, statusPaint)

                y += rowHeight
            }

            // Pie de página
            val footerY = PAGE_HEIGHT - 60f
            val footerPaint = Paint().apply {
                color = MUTED_COLOR
                textSize = 10f
                isAntiAlias = true
            }
            val currentDateTime = java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            canvas.drawText(
                "Generado el $currentDateTime por ObraCheck",
                MARGIN.toFloat(),
                footerY,
                footerPaint
            )
        }

        // ---- Acciones con el archivo ----

        /** Abrir el PDF directamente en un visor (recomendado para tu caso) */
        fun openPDF(context: Context, file: File) {
            try {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/pdf")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                try {
                    Toast.makeText(
                        context,
                        "No se encontró una app para abrir PDF.",
                        Toast.LENGTH_LONG
                    ).show()
                } catch (_: Exception) { /* no-op */ }
            }
        }

        /** Compartir el PDF (por si alguna vez lo necesitas) */
        fun sharePDF(context: Context, file: File) {
            try {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    putExtra(Intent.EXTRA_SUBJECT, "Reporte de Asistencia - ObraCheck")
                    putExtra(Intent.EXTRA_TEXT, "Reporte de asistencia generado por ObraCheck")
                }
                context.startActivity(Intent.createChooser(intent, "Compartir reporte"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
