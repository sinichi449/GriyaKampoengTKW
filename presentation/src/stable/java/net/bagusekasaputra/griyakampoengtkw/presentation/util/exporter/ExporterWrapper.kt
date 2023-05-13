package net.bagusekasaputra.griyakampoengtkw.presentation.util.exporter

import android.content.Context
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran

/**
 * Bridging for Developer and Stable Build
 */
object ExporterWrapper {

    fun exportPembayaran(
        context: Context,
        kavlingKode: String,
        dataDiri: DataDiri,
        hargaKavling: HargaKavling,
        pembayarans: List<Pembayaran>
    ) {
        ExcelDataExporter(context).exportPembayaran(kavlingKode, dataDiri, hargaKavling, pembayarans)
    }

}