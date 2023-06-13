package net.bagusekasaputra.griyakampoengtkw.data.model

data class PengembalianModel(
    val keyId: String = "",
    val kavling: String = "",
    val namaCustomer: String = "",
    val tanggal: String = "",
    val jumlah: Long = 0L,
    val keterangan: String = "",
    val uri: String = "",
    val timeMillis: Long = 0L,
) {
    val fileName = "${keyId}.png"

    companion object {
        const val DST_DIR = "pengembalian_images"
    }
}