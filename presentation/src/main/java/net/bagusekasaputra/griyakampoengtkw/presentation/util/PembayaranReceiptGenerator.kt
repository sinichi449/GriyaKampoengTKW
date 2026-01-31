package net.bagusekasaputra.griyakampoengtkw.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toSlashedString
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.BulanAngsuran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.R
import net.bagusekasaputra.griyakampoengtkw.presentation.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil.numericToString
import net.bagusekasaputra.griyakampoengtkw.domain.entity.PembayaranTambahLuasan
import androidx.core.graphics.createBitmap

object PembayaranReceiptGenerator {

    // Dimensions
    private const val ROW_HEIGHT = 120f
    private const val HEADER_ROW_HEIGHT = 140f
    private const val FONT_SIZE = 40f
    private const val TITLE_FONT_SIZE = 48f
    private const val PADDING = 32f
    private const val LINE_SPACING = 48f
    private const val BOTTOM_BUFFER = 100f
    private const val SECTION_TITLE_HEIGHT = 100f

    // Column Config
    private val COL_WIDTH_PERCENTS = listOf(0.05f, 0.12f, 0.12f, 0.12f, 0.15f, 0.15f, 0.10f, 0.19f)
    private val HEADERS = listOf("No", "Termin", "Invoice", "Tanggal", "Uang Dibayar", "Total", "Persentase", "Keterangan")

    // Column Config - TAMBAHAN TABLE (Adjusted for simpler data)
    private val ADD_COL_WIDTHS = listOf(0.10f, 0.25f, 0.25f, 0.40f)
    private val ADD_HEADERS = listOf("No", "Tanggal", "Jumlah", "Keterangan")

    fun generateBitmap(
        context: Context,
        data: List<Pembayaran>,
        tambahanData: List<PembayaranTambahLuasan>?,
        headerLines: List<String>, // e.g. ["Nama : Bagus", "Kavling : C4"]
        footerLines: List<String>  // e.g. ["Sisa Waktu : 10 Bulan", "Total Belum Bayar : Rp 100.000"]
    ): Bitmap {

        // --- 1. Setup Paints ---
        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = FONT_SIZE
            typeface = Typeface.DEFAULT
            isAntiAlias = true
        }

        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = TITLE_FONT_SIZE
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        // New Paint for Section Titles
        val subtitlePaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 48f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }

        val tableHeaderBgPaintAngsuran = Paint().apply {
            color = ContextCompat.getColor(context, R.color.gkt1_primary)
            style = Paint.Style.FILL
        }

        val tableHeaderBgPaintTambahan = Paint().apply {
            color = ContextCompat.getColor(context, R.color.secondaryColor)
            style = Paint.Style.FILL
        }

        val rowPaintSelected = Paint().apply {
            color = ContextCompat.getColor(context, R.color.table_unselected_colour) // White
            style = Paint.Style.FILL
        }

        val rowPaintNormal = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }

        val linePaint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 2f
            style = Paint.Style.STROKE
        }

        // --- 2. Calculate Dimensions ---
        val totalWidth = 2480 // A4 Width approx

        /// Top Section
        var totalHeight = (headerLines.size * LINE_SPACING) + (PADDING * 2) + TITLE_FONT_SIZE

        // Table 1 (Angsuran)
        totalHeight += SECTION_TITLE_HEIGHT
        totalHeight += HEADER_ROW_HEIGHT
        totalHeight += (data.size * ROW_HEIGHT)
        totalHeight += PADDING

        // Footer Section (Moved here: Belong to Table 1)
        totalHeight += (footerLines.size * LINE_SPACING) + PADDING

        // Table 2 (Tambahan) - If Exists
        val hasTambahan = !tambahanData.isNullOrEmpty()
        if (hasTambahan) {
            totalHeight += PADDING // Gap before Table 2 starts
            totalHeight += SECTION_TITLE_HEIGHT
            totalHeight += HEADER_ROW_HEIGHT
            totalHeight += (tambahanData!!.size * ROW_HEIGHT)
            totalHeight += PADDING
        }

        totalHeight += BOTTOM_BUFFER

        val bitmap = createBitmap(totalWidth, totalHeight.toInt(), Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        // --- 3. Draw Top Info ---
        var currentY = PADDING + TITLE_FONT_SIZE
        headerLines.forEach { line ->
            canvas.drawText(line, PADDING, currentY, titlePaint)
            currentY += LINE_SPACING
        }
        currentY += PADDING

        // --- 4. Draw Table 1: ANGSURAN ---

        // Section Title
        canvas.drawText("Pembayaran Angsuran", PADDING, currentY, subtitlePaint)
        currentY += (SECTION_TITLE_HEIGHT / 2)

        // Headers
        drawRow(canvas, 0f, currentY, totalWidth, COL_WIDTH_PERCENTS, HEADERS, tableHeaderBgPaintAngsuran, linePaint, textPaint, isHeader = true)
        currentY += HEADER_ROW_HEIGHT

        // Rows
        data.forEachIndexed { index, item ->
            val bgPaint = if (item.sudahIsiFotoPembayaran) rowPaintSelected else rowPaintNormal

            val rowData = listOf(
                (index + 1).toString(),
                item.termin + if(item.sudahAmbilKuitansi) " {*}" else "",
                BulanAngsuran.fromDate(item.bulanAngsuran.date).bulanAndTahun,
                item.tanggal.toDate().toSlashedString(),
                item.jumlahUangDibayar,
                item.totalUangMasuk,
                "${item.presentase}%",
                item.keterangan
            )

            drawRow(canvas, 0f, currentY, totalWidth, COL_WIDTH_PERCENTS, rowData, bgPaint, linePaint, textPaint, isHeader = false)
            currentY += ROW_HEIGHT
        }

        // --- 5. Draw Footer (MOVED HERE) ---
        // Drawn immediately below Table 1
        currentY += PADDING
        footerLines.forEach { line ->
            canvas.drawText(line, PADDING, currentY + TITLE_FONT_SIZE, titlePaint)
            currentY += LINE_SPACING
        }

        // --- 6. Draw Table 2: TAMBAHAN LUASAN (If Exists) ---
        if (hasTambahan) {
            currentY += PADDING // Gap separating Footer from Table 2 Title

            // Section Title
            canvas.drawText("Pembayaran Tambahan Luasan", PADDING, currentY + (SECTION_TITLE_HEIGHT/2), subtitlePaint)
            currentY += SECTION_TITLE_HEIGHT

            // Headers
            drawRow(canvas, 0f, currentY, totalWidth, ADD_COL_WIDTHS, ADD_HEADERS, tableHeaderBgPaintTambahan, linePaint, textPaint, isHeader = true)
            currentY += HEADER_ROW_HEIGHT

            // Rows
            tambahanData!!.forEachIndexed { index, item ->

                val rowData = listOf(
                    (index + 1).toString(),
                    item.tanggal.toDate().toSlashedString(),
                    item.jumlahUang.numericToString(),
                    item.keterangan,
                )

                drawRow(canvas, 0f, currentY, totalWidth, ADD_COL_WIDTHS, rowData,
                    rowPaintNormal, linePaint, textPaint, isHeader = false)
                currentY += ROW_HEIGHT
            }
        }

        return bitmap
    }

    // Helper to draw a single row (Header or Data) for ANY table config
    private fun drawRow(
        canvas: Canvas, x: Float, y: Float, totalWidth: Int,
        widths: List<Float>, texts: List<String>,
        bgPaint: Paint, linePaint: Paint, textPaint: Paint, isHeader: Boolean
    ) {
        val rowH = if(isHeader) HEADER_ROW_HEIGHT else ROW_HEIGHT
        var currentX = x

        // Draw Background
        canvas.drawRect(x, y, totalWidth.toFloat(), y + rowH, bgPaint)

        // Draw Columns
        val fontPaint = if(isHeader) Paint(textPaint).apply {
            color = Color.WHITE; typeface = Typeface.DEFAULT_BOLD
        } else textPaint

        widths.forEachIndexed { index, percent ->
            val colWidth = totalWidth * percent
            val text = texts.getOrElse(index) { "" }

            canvas.drawRect(currentX, y, currentX + colWidth, y + rowH, linePaint)

            // Center text (unless it's the last column, then Left Align)
            if (index == widths.size - 1 && !isHeader) {
                val paddingX = currentX + (PADDING / 2)
                val textY = y + (rowH / 2) - ((fontPaint.descent() + fontPaint.ascent()) / 2)
                canvas.drawText(text, paddingX, textY, fontPaint)
            } else {
                drawTextCentered(canvas, text, currentX, y, colWidth, rowH, fontPaint) // Reuse your existing helper
            }
            currentX += colWidth
        }
    }

    private fun drawTextCentered(canvas: Canvas, text: String, x: Float, y: Float, w: Float, h: Float, paint: Paint) {
        val textWidth = paint.measureText(text)
        val startX = x + (w - textWidth) / 2
        val startY = y + (h / 2) - ((paint.descent() + paint.ascent()) / 2)
        canvas.drawText(text, startX, startY, paint)
    }
}