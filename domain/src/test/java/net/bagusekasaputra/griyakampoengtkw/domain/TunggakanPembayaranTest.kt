package net.bagusekasaputra.griyakampoengtkw.domain

import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import org.junit.Test
import java.util.Calendar

class TunggakanPembayaranTest {

    data class TunggakanPembayaran(
        val bulan: String,
        val jumlah: Long,
    )

    @Test
    fun balbalbalba() {
//        val listPembayaran = getListPembayaran()
//        val baseline = getBaselinePembayaran()
//
//        listPembayaran.forEach { pembayaran ->
//            val tanggalDibayar = pembayaran.tanggal.toDate()
//            val bulanPembayaran = Calendar.getInstance().apply {
//                time = tanggalDibayar
//            }.get(Calendar.MONTH)
//
//            val listTanggal = getListTanggal(bulanPembayaran)
//
//
//        }
    }

    @Test
    fun grouping_list_pembayaran_into_pembayaran_per_bulan() {

    }
}