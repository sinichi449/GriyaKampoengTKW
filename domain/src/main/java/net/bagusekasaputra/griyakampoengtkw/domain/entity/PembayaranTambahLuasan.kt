package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import java.util.UUID

data class PembayaranTambahLuasan(
    val id: String = UUID.randomUUID().toString(),
    val kavling: String,
    val fotoUri: String = "",
    val sudahAmbilKuitansi: Boolean = false,
    val tanggal: String,
    val jumlahUang: Long,
    val keterangan: String = "-",
    val timeMillis: Long = System.currentTimeMillis()
) {


    companion object {
        fun sortByTanggal(entities: List<PembayaranTambahLuasan>): List<PembayaranTambahLuasan> {
            return entities.sortedBy {
                it.tanggal.toDate().time
            }
        }
    }

}