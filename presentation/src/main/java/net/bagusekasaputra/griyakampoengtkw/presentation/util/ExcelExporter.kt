package net.bagusekasaputra.griyakampoengtkw.presentation.util

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes.Companion.LOG_TAG
import org.apache.poi.hssf.usermodel.HSSFFont
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.util.CellRangeAddress
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ExcelExporter(
//    private val blockKode: String,
//    private val kavlingNumber: String,
//    private val namaPembayar: String,
//    private val hargaKavling: String,
//    private val tambahLuasan: String,
//    private val totalHarga: String,
//    private val sisaBelumTerbayar: String,
    private val dataPembayaran: List<Pembayaran>,
) {

    fun storeExcelInStorage(ctx: Context, workbook: Workbook, fileName: String): Boolean {
        var isSuccess: Boolean

        val file = File(ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
        var fileOutputStream: FileOutputStream? = null

        try {
            fileOutputStream = FileOutputStream(file)

            workbook.write(fileOutputStream)
            Log.d(LOG_TAG, "Writing file: $file")

            isSuccess = true

            Toast.makeText(ctx, "File saved to: $file", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Log.d(LOG_TAG, "Error writing: ${e.message}")
            Toast.makeText(ctx, "Error writing file: ${e.message}", Toast.LENGTH_LONG).show()

            isSuccess = false
        } catch (e: Exception) {
            Log.d(LOG_TAG, "Failed to save file: ${e.message}")
            Toast.makeText(ctx, "Failed to save file: ${e.message}", Toast.LENGTH_LONG).show()

            isSuccess = false
        } finally {
            try {
                fileOutputStream?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return isSuccess
    }

    fun createPembayaranSpreadsheet(): Workbook {
        val headers = getHeaderPembayaran()

        val headerTitlePosition = 0
        val subtitleHeaderPosition = 1
//        val hargaRowPosition = 2
//        val tambahLuasanRowPosition = 3
//        val sisaBelumTerbayarRowPosition = HashMap<String, Int>().apply {
//            put("header_row", 3)
//            put("header_column", 2)
//            put("content_row", 4)
//            put("content_column", 2)
//        }
//        val totalHargaPosition = HashMap<String, Int>().apply {
//            put("content_row", 4)
//            put("content_column", 1)
//        }
        val dataRowHeaderPosition = 6
//        val dataRowCellsPosition = 7


        val mergedCellSize = headers.size-1

        val workbook: Workbook = HSSFWorkbook()
        val pembayaranSheet: Sheet = workbook.createSheet("Sheet1")

        /**
         * Title Header
         */
        val titleRange = CellRangeAddress(headerTitlePosition, headerTitlePosition, 0, mergedCellSize)
        pembayaranSheet.addMergedRegion(titleRange)
//        val titleHeader = pembayaranSheet.createRow(headerTitlePosition)
//        val titleCell = titleHeader.createCell(0).apply {
//            val cellStyle = workbook.createCellStyle()
//            val fontStyle = workbook.createFont()
//
//            fontStyle.boldweight = Font.BOLDWEIGHT_BOLD
//            cellStyle.alignment = CellStyle.ALIGN_CENTER
//
//            cellStyle.setFont(fontStyle)
//
//            this.cellStyle = cellStyle
//            this.setCellValue("FORM PEMBAYARAN GRIYA KAMPOENG TKW BLOK. $blockKode KAV.$kavlingNumber")
//        }


        /**
         * Subtitle Header
         */
        val subtitleRange = CellRangeAddress(subtitleHeaderPosition, subtitleHeaderPosition, 0, mergedCellSize)
        pembayaranSheet.addMergedRegion(subtitleRange)
//        val subtitleRow = pembayaranSheet.createRow(subtitleHeaderPosition)
//        val subtitleCell = subtitleRow.createCell(0).apply {
//            val cellStyle = workbook.createCellStyle()
//
//            cellStyle.alignment = CellStyle.ALIGN_CENTER
//
//            this.cellStyle = cellStyle
//            this.setCellValue(namaPembayar.uppercase())
//        }


        /**
         * Harga
         */
//        val hargaRow = pembayaranSheet.createRow(hargaRowPosition).apply {
//            // Harga title
//            this.createCell(0).apply {
//                this.setCellValue("Harga")
//            }
//            // Harga value
//            this.createCell(1).apply {
//                this.setCellValue(hargaKavling)
//            }
//        }


        /**
         * Tambah Luasan
         */
//        val tambahLuasanRow = pembayaranSheet.createRow(tambahLuasanRowPosition).apply {
//            // Tambah Luasan title
//            this.createCell(0).apply {
//                this.setCellValue("Tambah Luasan")
//            }
//            // Tambah Luasan value
//            this.createCell(1).apply {
//                val cellStyle = workbook.createCellStyle()
//                cellStyle.borderBottom = CellStyle.BORDER_THIN
//
//                this.cellStyle = cellStyle
//
//                this.setCellValue(tambahLuasan)
//            }
//
//            /**
//             * Sisa belum terbayar header
//             */
//            this.createCell(sisaBelumTerbayarRowPosition["header_column"]!!).apply {
//                val fontStyle = workbook.createFont().apply {
//                    boldweight = HSSFFont.BOLDWEIGHT_BOLD
//                }
//                val cellStyle = workbook.createCellStyle().apply {
//                    alignment = CellStyle.ALIGN_CENTER
//                    setFont(fontStyle)
//                }
//
//                this.cellStyle = cellStyle
//                this.setCellValue("Sisa blm terbayar")
//            }
//        }


        /**
         * Total
         */
//        val totalRow = pembayaranSheet.createRow(totalHargaPosition["content_row"]!!).apply {
//            // Total value
//            this.createCell(totalHargaPosition["content_column"]!!).apply {
//                this.setCellValue(totalHarga)
//            }
//
//            /**
//             * Sisa belum terbayar value
//             */
//            this.createCell(sisaBelumTerbayarRowPosition["content_column"]!!).apply {
//                val cellStyle = workbook.createCellStyle().apply {
//                    alignment = CellStyle.ALIGN_RIGHT
//                    fillBackgroundColor = IndexedColors.DARK_YELLOW.index
//                }
//
//                this.cellStyle = cellStyle
//                this.setCellValue(sisaBelumTerbayar)
//            }
//        }


        /**
         * Pembayaran Data Header
         */
        val headerRows = pembayaranSheet.createRow(dataRowHeaderPosition)

        headers.forEachIndexed { index, header ->
            val headerCell = headerRows.createCell(index)
            headerCell.cellStyle = getHeaderCellStyle(workbook)

            headerCell.setCellValue(header)
        }


        /**
         * Pembayaran List Data
         */
        dataPembayaran.forEachIndexed { _, _ ->
//            val dataRow = pembayaranSheet.createRow(index + dataRowCellsPosition)

//            val terminCell = createDataCell(workbook, dataRow, 0, pembayaran.termin)
//            val tanggalCell = createDataCell(workbook, dataRow, 1, pembayaran.tanggal)
//            val jumlahUangDibayarCell = createDataCell(workbook, dataRow, 2, pembayaran.jumlahUangDibayar)
//            val totalUangMasukCell = createDataCell(workbook, dataRow, 3, pembayaran.totalUangMasuk)
//            val persentaseCell = createDataCell(workbook, dataRow, 4, "${pembayaran.presentase.toString()}%")
//            val keteranganCell = createDataCell(workbook, dataRow, 5, pembayaran.keterangan)
        }


        // TODO: Sizing Columns


        return workbook
    }

    private fun getHeaderPembayaran(): List<String> {
        return listOf(
            "Termin Ke",
            "Tanggal",
            "Jumlah Uang Dibayar",
            "Total Uang Masuk",
            "Persentase",
            "Keterangan Progress"
        )
    }

    private fun getHeaderCellStyle(workbook: Workbook): CellStyle {
        val headerFont: Font = workbook.createFont().apply {
            this.fontName = "Arial"
            this.boldweight = HSSFFont.BOLDWEIGHT_BOLD
            this.italic = false
        }
        val headerCellStyle: CellStyle = workbook.createCellStyle().apply {
            this.alignment = CellStyle.ALIGN_CENTER
            setAllBorder(this, CellStyle.BORDER_THIN)
            this.setFont(headerFont)
        }

        return headerCellStyle
    }

//    private fun getDataCellStyle(workbook: Workbook, position: Int): CellStyle {
//        val dataFont: Font = workbook.createFont().apply {
//            this.fontName = "Arial"
//            this.color = IndexedColors.BLACK.index
//        }
//
//        val dataCellStyle: CellStyle = workbook.createCellStyle().apply {
//            this.setFont(dataFont)
//            setAllBorder(this, CellStyle.BORDER_THIN)
//
//            when (position) {
//                0, 1, 4 -> this.alignment = CellStyle.ALIGN_CENTER
//                2, 3 -> this.alignment = CellStyle.ALIGN_RIGHT
//                5 -> this.alignment = CellStyle.ALIGN_LEFT
//            }
//        }
//
//        return dataCellStyle
//    }

//    private fun createDataCell(workbook: Workbook, dataRow: Row, position: Int, cellValue: String): Cell {
//        return dataRow.createCell(position).apply {
//            this.cellStyle = getDataCellStyle(workbook, position)
//
//            this.setCellValue(cellValue)
//
//        }
//    }

    private fun setAllBorder(cellStyle: CellStyle, borderStyle: Short) {
        cellStyle.borderTop = borderStyle
        cellStyle.borderBottom = borderStyle
        cellStyle.borderRight = borderStyle
        cellStyle.borderLeft = borderStyle
    }
}