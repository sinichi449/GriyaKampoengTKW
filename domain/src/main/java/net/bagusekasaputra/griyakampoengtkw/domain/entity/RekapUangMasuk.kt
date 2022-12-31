package net.bagusekasaputra.griyakampoengtkw.domain.entity

data class RekapUangMasuk(
    val noKavling: String,
    val namaCostumer: String,
    val tanggal: String,
    val jenisPembayaran: String,
    val jumlahPembayaran: Long,
) {
     companion object {
         fun hitungTotal(listRekap: List<RekapUangMasuk>?): Long {
             var total = 0L

             listRekap?.forEach {
                 total += it.jumlahPembayaran
             }

             return total
         }
     }
}