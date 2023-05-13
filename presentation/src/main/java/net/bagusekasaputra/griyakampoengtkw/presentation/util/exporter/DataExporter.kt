package net.bagusekasaputra.griyakampoengtkw.presentation.util.exporter

import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran

interface DataExporter {

    fun exportPembayaran(
        kavlingKode: String,
        dataDiri: DataDiri,
        hargaKavling: HargaKavling,
        pembayarans: List<Pembayaran>,
    )

}