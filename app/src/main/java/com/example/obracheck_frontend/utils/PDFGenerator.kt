@file:Suppress("SpellCheckingInspection")

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
import com.example.obracheck_frontend.model.domain.Progress
import com.example.obracheck_frontend.model.domain.Evidence
import com.example.obracheck_frontend.model.dto.AttendanceStatus
import java.io.File
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Suppress("unused")
class PDFGenerator {

    companion object {
        private const val PAGE_WIDTH = 595
        private const val PAGE_HEIGHT = 842
        private const val MARGIN = 40
        private const val HEADER_HEIGHT = 80

        // Colores ObraCheck
        private const val BRAND_COLOR = 0xFF1F2A33.toInt()
        private const val ACCENT_COLOR = 0xFFF6C445.toInt()
        private const val SUCCESS_COLOR = 0xFF10B981.toInt()
        private const val DANGER_COLOR = 0xFFEF4444.toInt()
        private const val MUTED_COLOR = 0xFF7B8AA0.toInt()

        /**
         * Decodifica bytes de imagen con manejo de memoria eficiente
         */
        private fun decodeScaled(imageBytes: ByteArray?): Bitmap? {
            if (imageBytes == null || imageBytes.isEmpty()) return null

            return try {
                // Configuración para reducir uso de memoria
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = false
                    inSampleSize = 1 // Ajustar si necesitas reducir más la calidad
                    inPreferredConfig = Bitmap.Config.RGB_565 // Usa menos memoria que ARGB_8888
                }
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, options)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        /**
         * Escala un bitmap para que quepa dentro de las dimensiones especificadas
         * manteniendo la proporción de aspecto
         */
        private fun scaleToFitBitmap(bitmap: Bitmap, maxWidth: Float, maxHeight: Float): Bitmap {
            val originalWidth = bitmap.width.toFloat()
            val originalHeight = bitmap.height.toFloat()

            if (originalWidth <= maxWidth && originalHeight <= maxHeight) {
                return bitmap // Ya cabe, no necesita escalado
            }

            val widthRatio = maxWidth / originalWidth
            val heightRatio = maxHeight / originalHeight
            val scaleFactor = minOf(widthRatio, heightRatio)

            val newWidth = (originalWidth * scaleFactor).toInt()
            val newHeight = (originalHeight * scaleFactor).toInt()

            return try {
                Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            } catch (e: Exception) {
                e.printStackTrace()
                bitmap // Retorna el original si falla el escalado
            }
        }

        /**
         * Libera memoria del bitmap de forma segura
         */
        private fun safeBitmapRecycle(bitmap: Bitmap?) {
            try {
                if (bitmap != null && !bitmap.isRecycled) {
                    bitmap.recycle()
                }
            } catch (e: Exception) {
                // Ignorar errores al reciclar
            }
        }

        // FUNCIÓN PRINCIPAL: Generar reporte de progreso CON IMÁGENES
        suspend fun generateProgressReportWithImages(
            context: Context,
            progress: Progress,
            evidences: List<Evidence>,
            fetchImageBytes: suspend (evidenceId: Long) -> ByteArray?,
            onSuccess: (File) -> Unit,
            onError: (String) -> Unit
        ) {
            try {
                val file = withContext(Dispatchers.IO) {
                    val pdf = PdfDocument()
                    val pageW = PAGE_WIDTH
                    val pageH = PAGE_HEIGHT
                    val left = MARGIN.toFloat()
                    val right = (pageW - MARGIN).toFloat()
                    val contentWidth = right - left

                    // ======= PÁGINA 1: header + texto + evidencias (llenando sin huecos) =======
                    val info1 = PdfDocument.PageInfo.Builder(pageW, pageH, 1).create()
                    val page1 = pdf.startPage(info1)
                    val c1 = page1.canvas

                    val titlePaint = Paint().apply {
                        color = BRAND_COLOR; textSize = 24f
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); isAntiAlias = true
                    }
                    val subtitlePaint = Paint().apply { color = MUTED_COLOR; textSize = 16f; isAntiAlias = true }
                    val bodyPaint = Paint().apply { color = BRAND_COLOR; textSize = 12f; isAntiAlias = true }
                    val accent = Paint().apply { color = ACCENT_COLOR }

                    val evTitle = Paint().apply {
                        color = BRAND_COLOR; textSize = 14f
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD); isAntiAlias = true
                    }
                    val notePaint = Paint().apply { color = MUTED_COLOR; textSize = 10f; isAntiAlias = true }

                    var y = MARGIN + 20f

                    // Barra superior
                    c1.drawRect(MARGIN.toFloat(), MARGIN.toFloat(), (pageW - MARGIN).toFloat(), MARGIN + 6f, accent)

                    // Título e info
                    c1.drawText("REPORTE DE PROGRESO", left, y, titlePaint); y += 28f
                    c1.drawText("Obra: ${progress.site.name}", left, y, subtitlePaint); y += 18f
                    c1.drawText("Trabajador: ${progress.worker.name}", left, y, subtitlePaint); y += 18f
                    c1.drawText("Fecha: ${progress.date.take(10)}", left, y, subtitlePaint); y += 22f

                    // Descripción (máx 3 líneas)
                    c1.drawText("Descripción:", left, y, bodyPaint); y += 16f
                    run {
                        val desc = progress.description
                        val lines = mutableListOf<String>()
                        var line = ""
                        for (w in desc.split("\\s+".toRegex())) {
                            if ((line + " " + w).length <= 90) line = if (line.isEmpty()) w else "$line $w"
                            else { lines.add(line); line = w; if (lines.size >= 3) break }
                        }
                        if (lines.size < 3 && line.isNotEmpty()) lines.add(line)
                        lines.forEach { c1.drawText(it, left + 16f, y, bodyPaint).also { y += 15f } }
                    }
                    y += 10f
                    c1.drawText("Total de evidencias: ${evidences.size}", left, y, bodyPaint); y += 25f

                    // ---- Cálculo de layout por bloques (uniforme) ----
                    // Estructura de cada bloque: título (14) + imagen en caja fija + pie (12) + separación (15)
                    val nonImgSpace = 14f + 20f + 15f   // = 49f (más espacio)
                    val topOffsetToImage = 14f         // desde título a imagen

                    // Altura disponible en la página 1 (desde y hasta el margen inferior)
                    val availFirst = pageH - MARGIN - y
                    val minImgBox = 140f
                    val maxImgBox = 360f

                    // ¿Cuántos bloques caben en la primera página?
                    var blocksFirst = ((availFirst) / (nonImgSpace + minImgBox)).toInt().coerceAtLeast(1)
                    // Calcula la altura de la caja de imagen para ocupar toda la página sin huecos visibles
                    var imgBoxFirst = ((availFirst - blocksFirst * (nonImgSpace)) / blocksFirst)
                        .coerceIn(minImgBox, maxImgBox)

                    // Dibuja hasta blocksFirst evidencias (o menos si hay pocas)
                    var drawn = 0
                    while (drawn < evidences.size && drawn < blocksFirst) {
                        val ev = evidences[drawn]

                        // Título (sin extensión)
                        val cleanFileName = ev.fileName.replace(Regex("\\.(jpg|jpeg|png|gif|bmp)$", RegexOption.IGNORE_CASE), "")
                        c1.drawText(cleanFileName, left, y, evTitle)

                        // Imagen dentro de caja uniforme
                        var originalBmp: Bitmap? = null
                        var scaledBmp: Bitmap? = null

                        try {
                            originalBmp = decodeScaled(fetchImageBytes(ev.id))
                            val imgTop = y + topOffsetToImage

                            if (originalBmp != null) {
                                scaledBmp = scaleToFitBitmap(originalBmp, contentWidth, imgBoxFirst)
                                val vPad = ((imgBoxFirst - scaledBmp.height).coerceAtLeast(0f)) / 2f
                                val imgLeft = left + (contentWidth - scaledBmp.width) / 2f
                                c1.drawBitmap(scaledBmp, imgLeft, imgTop + vPad, null)
                            } else {
                                c1.drawText("No se pudo cargar la imagen.", left, imgTop + 12f, notePaint)
                            }
                        } catch (e: Exception) {
                            val imgTop = y + topOffsetToImage
                            c1.drawText("Error al cargar imagen: ${e.message}", left, imgTop + 12f, notePaint)
                        } finally {
                            // Liberar memoria
                            if (scaledBmp != null && scaledBmp !== originalBmp) {
                                safeBitmapRecycle(scaledBmp)
                            }
                            safeBitmapRecycle(originalBmp)
                        }

                        // Pie (fecha) - con más espacio
                        val footY = y + topOffsetToImage + imgBoxFirst + 20f
                        c1.drawText("Fecha: ${ev.uploadDate.take(10)}", left, footY, notePaint)

                        // Avanza al siguiente bloque
                        y = footY + 15f
                        drawn++
                    }

                    pdf.finishPage(page1)

                    // ======= PÁGINAS SIGUIENTES (mismo principio: llenar sin huecos) =======
                    val availNext = pageH - 2 * MARGIN
                    var blocksPerPage = ((availNext) / (nonImgSpace + minImgBox)).toInt().coerceAtLeast(1)
                    var imgBoxNext = ((availNext - blocksPerPage * (nonImgSpace)) / blocksPerPage)
                        .coerceIn(minImgBox, maxImgBox)

                    var pageNum = 2
                    var index = drawn
                    while (index < evidences.size) {
                        val info = PdfDocument.PageInfo.Builder(pageW, pageH, pageNum++).create()
                        val page = pdf.startPage(info)
                        val c = page.canvas
                        var yy = MARGIN.toFloat()

                        var blocksHere = 0
                        while (index < evidences.size && blocksHere < blocksPerPage) {
                            val ev = evidences[index]

                            // Título (sin extensión)
                            val cleanFileName = ev.fileName.replace(Regex("\\.(jpg|jpeg|png|gif|bmp)$", RegexOption.IGNORE_CASE), "")
                            c.drawText(cleanFileName, left, yy, evTitle)

                            var originalBmp: Bitmap? = null
                            var scaledBmp: Bitmap? = null

                            try {
                                originalBmp = decodeScaled(fetchImageBytes(ev.id))
                                val imgTop = yy + topOffsetToImage

                                if (originalBmp != null) {
                                    scaledBmp = scaleToFitBitmap(originalBmp, contentWidth, imgBoxNext)
                                    val vPad = ((imgBoxNext - scaledBmp.height).coerceAtLeast(0f)) / 2f
                                    val imgLeft = left + (contentWidth - scaledBmp.width) / 2f
                                    c.drawBitmap(scaledBmp, imgLeft, imgTop + vPad, null)
                                } else {
                                    c.drawText("No se pudo cargar la imagen.", left, imgTop + 12f, notePaint)
                                }
                            } catch (e: Exception) {
                                val imgTop = yy + topOffsetToImage
                                c.drawText("Error al cargar imagen: ${e.message}", left, imgTop + 12f, notePaint)
                            } finally {
                                // Liberar memoria
                                if (scaledBmp != null && scaledBmp !== originalBmp) {
                                    safeBitmapRecycle(scaledBmp)
                                }
                                safeBitmapRecycle(originalBmp)
                            }

                            // Pie (fecha) - con más espacio
                            val footY = yy + topOffsetToImage + imgBoxNext + 20f
                            c.drawText("Fecha: ${ev.uploadDate.take(10)}", left, footY, notePaint)

                            yy = footY + 15f
                            index++
                            blocksHere++
                        }

                        pdf.finishPage(page)
                    }

                    // Guardar archivo
                    val safeSite = progress.site.name.replace("\\s+".toRegex(), "_")
                    val safeDesc = progress.description.take(20).replace("\\s+".toRegex(), "_")
                    val fileName = "progreso_${progress.id}_${safeSite}_${safeDesc}_${progress.date.take(10)}.pdf"
                    val outDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: context.filesDir
                    val file = File(outDir, fileName)
                    FileOutputStream(file).use { pdf.writeTo(it) }
                    pdf.close()
                    file
                }

                onSuccess(file)
            } catch (e: Exception) {
                e.printStackTrace()
                onError("No se pudo generar PDF con imágenes: ${e.message ?: "desconocido"}")
            }
        }

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

                var currentY = MARGIN.toFloat()

                drawHeader(canvas, titlePaint, subtitlePaint, accentPaint, siteName, date, currentY)
                currentY += HEADER_HEIGHT + 20

                currentY = drawSummary(canvas, attendances, headerPaint, bodyPaint, currentY)
                currentY += 30

                drawAttendanceTable(
                    canvas,
                    attendances,
                    headerPaint,
                    bodyPaint,
                    accentPaint,
                    currentY
                )

                pdfDocument.finishPage(page)

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

            canvas.drawRect(
                MARGIN.toFloat(),
                startY,
                (PAGE_WIDTH - MARGIN).toFloat(),
                startY + 6,
                accentPaint
            )

            canvas.drawText("REPORTE DE ASISTENCIA", MARGIN.toFloat(), y, titlePaint)
            y += 30

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

            canvas.drawText("RESUMEN", MARGIN.toFloat(), y, headerPaint)
            y += 25

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
            val columnWidths = floatArrayOf(50f, 220f, 120f, 120f)

            canvas.drawText("LISTA DE TRABAJADORES", MARGIN.toFloat(), y, headerPaint)
            y += 30

            var x = MARGIN.toFloat()
            val headerY = y
            canvas.drawText("#", x + 10, headerY, headerPaint); x += columnWidths[0]
            canvas.drawText("Nombre", x + 10, headerY, headerPaint); x += columnWidths[1]
            canvas.drawText("Cédula", x + 10, headerY, headerPaint); x += columnWidths[2]
            canvas.drawText("Estado", x + 10, headerY, headerPaint)

            y += 5
            canvas.drawRect(
                MARGIN.toFloat(),
                y,
                MARGIN + columnWidths.sum(),
                y + 2,
                accentPaint
            )
            y += rowHeight

            val zebraBg = "#F9FAFB".toColorInt()
            val lateColor = "#F59E0B".toColorInt()

            attendances.takeWhile { y <= PAGE_HEIGHT - 100 }.forEachIndexed { index, attendance ->
                x = MARGIN.toFloat()
                val rowY = y

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

                canvas.drawText((index + 1).toString(), x + 10, rowY, bodyPaint)
                x += columnWidths[0]

                val name = if (attendance.workerName.length > 28) {
                    attendance.workerName.take(25) + "..."
                } else attendance.workerName
                canvas.drawText(name, x + 10, rowY, bodyPaint)
                x += columnWidths[1]

                canvas.drawText(attendance.ci, x + 10, rowY, bodyPaint)
                x += columnWidths[2]

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
                } catch (_: Exception) {
                }
            }
        }

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
                    putExtra(Intent.EXTRA_SUBJECT, "Reporte ObraCheck")
                    putExtra(Intent.EXTRA_TEXT, "Reporte generado por ObraCheck")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                val chooserIntent = Intent.createChooser(intent, "Compartir PDF")
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(chooserIntent)

            } catch (e: Exception) {
                e.printStackTrace()
                try {
                    Toast.makeText(
                        context,
                        "Error al compartir el archivo PDF",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (_: Exception) {
                    // Ignore si no se puede mostrar el Toast
                }
            }
        }
    }
}