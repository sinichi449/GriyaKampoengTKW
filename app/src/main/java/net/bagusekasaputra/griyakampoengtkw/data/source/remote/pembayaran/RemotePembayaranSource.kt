package net.bagusekasaputra.griyakampoengtkw.data.source.remote.pembayaran

import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel

interface RemotePembayaranSource {

    suspend fun getAllPembayaran(
        kavlingKode: String,
        onSuccess: (listPembayaranModel: List<PembayaranModel>?) -> Unit,
        onFailure: (throwable: Throwable) -> Unit,
    )

    suspend fun addPembayaranModel(
        kavlingKode: String,
        hargaKavling: Long,
        pembayaranModel: PembayaranModel,
        onSuccess: () -> Unit,
        onFailure: (throwable: Throwable) -> Unit,
    )

}