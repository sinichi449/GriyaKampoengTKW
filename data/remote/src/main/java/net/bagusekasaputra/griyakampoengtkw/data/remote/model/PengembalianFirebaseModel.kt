package net.bagusekasaputra.griyakampoengtkw.data.remote.model

import net.bagusekasaputra.griyakampoengtkw.data.model.PengembalianModel
import net.bagusekasaputra.griyakampoengtkw.data.model.PengembalianModel.Companion.parseJumlahUang

data class PengembalianFirebaseModel(
    val jumlah: String = "",
    val kavling: String = "",
    val keterangan: String = "",
    val namaCustomer: String = "",
    val tanggal: String = "",
    val timeMillis: Long = 0L,
) {

    companion object {
        fun PengembalianFirebaseModel.toDataModel(keyId: String, uri: String = ""): PengembalianModel {
            return PengembalianModel(
                keyId = keyId,
                kavling = kavling,
                namaCustomer = namaCustomer,
                tanggal = tanggal,
                jumlah = jumlah.parseJumlahUang(),
                keterangan = keterangan,
                uri = uri,
                timeMillis = timeMillis,
            )
        }

        fun PengembalianModel.toFirebaseModel(): PengembalianFirebaseModel {
            return PengembalianFirebaseModel(
                jumlah = jumlah.parseJumlahUang(),
                kavling = kavling,
                keterangan = keterangan,
                namaCustomer = namaCustomer,
                tanggal = tanggal,
                timeMillis = timeMillis,
            )
        }
    }
}