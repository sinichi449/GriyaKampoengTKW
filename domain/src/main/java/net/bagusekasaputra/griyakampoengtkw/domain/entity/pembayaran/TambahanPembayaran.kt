package net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran

import java.util.Date
import java.util.UUID

data class TambahanPembayaran(
    val id: String = UUID.randomUUID().toString(),
    val kavling: String,
    val kategori: Kategori,
    val tanggal: Date,
    val jumlahUang: Long,
    val sudahIsiFoto: Boolean,
    val keterangan: String,
    val timeMillis: Long = System.currentTimeMillis(),
) {

    enum class Kategori(val kode: String) {
        LUASAN("L"),
        PEMBANGUNAN("P"),
    }

    companion object {
        fun getKategoriFromKode(kode: String): Kategori {
            return when(kode) {
                "L" -> Kategori.LUASAN
                "P" -> Kategori.PEMBANGUNAN
                else -> Kategori.PEMBANGUNAN
            }
        }

        suspend fun mask(
            listTambahan: List<TambahanPembayaran>,
            onCekFoto: suspend (kavling: String, id: String) -> Boolean,
        ): List<TambahanPembayaran> {
            // Sort by tanggal
            val sortedList = listTambahan.sortedBy {
                it.tanggal.time
            }
            val maskedList = buildList {
                sortedList.forEach {
                    // Update this class as cek foto
                    val updatedEntity = it.copy(
                        sudahIsiFoto = onCekFoto(it.kavling, it.id)
                    )
                    add(updatedEntity)
                }
            }

            return maskedList
        }
    }
}