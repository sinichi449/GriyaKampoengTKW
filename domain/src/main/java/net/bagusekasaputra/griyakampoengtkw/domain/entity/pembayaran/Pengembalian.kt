package net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran

import net.bagusekasaputra.griyakampoengtkw.domain.IdUtil
import java.util.Date

data class Pengembalian(
    val keyId: String = IdUtil.generateKeyId(),
    val kavling: String,
    val namaCustomer: String = "",
    val tanggal: Date,
    val jumlah: Long,
    val keterangan: String,
    val uri: String = "",
    val timeMillis: Long = System.currentTimeMillis(),
) {

    companion object {
        fun List<Pengembalian>.total(): Long {
            var total = 0L
            this.forEach { item ->
                total += item.jumlah
            }
            return total
        }
    }
}