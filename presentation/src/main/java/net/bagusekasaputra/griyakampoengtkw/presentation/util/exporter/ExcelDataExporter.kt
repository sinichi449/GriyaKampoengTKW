package net.bagusekasaputra.griyakampoengtkw.presentation.util.exporter

import android.content.Context
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.presentation.util.ExcelExporter

class ExcelDataExporter(
    private val context: Context
): DataExporter {

    override fun exportPembayaran(
        kavlingKode: String,
        dataDiri: DataDiri,
        hargaKavling: HargaKavling,
        pembayarans: List<Pembayaran>
    ) {
//        val blockKode = kavlingKode.substring(0)
//        val kavlingNum = kavlingKode.substring(1)
//
//        val sisaBelumBayar = if (pembayarans.isEmpty()) "0" else Pembayaran.getSisaBelumTerbayar(pembayarans)
        val excelExporter = ExcelExporter(
//            blockKode = blockKode,
//            kavlingNumber = kavlingNum,
//            namaPembayar = dataDiri.nama,
//            hargaKavling = hargaKavling.harga,
//            tambahLuasan = hargaKavling.tambahanLuas,
//            totalHarga = NumberUtil.formatLongToString(hargaKavling.hargaDanTambahLuasan),
//            sisaBelumTerbayar = sisaBelumBayar,
            dataPembayaran = pembayarans
        )
        val workbook = excelExporter.createPembayaranSpreadsheet()

        excelExporter.storeExcelInStorage(context,workbook, "pembayaran_$kavlingKode.xls")
    }
}