package net.bagusekasaputra.griyakampoengtkw.data.source.remote.pembayaran

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.data.source.model.PembayaranModel

interface RemotePembayaranSource {

    fun addPembayaranModel(kavlingKode: String, pembayaranModel: PembayaranModel): Flow<Result<Boolean>>

    fun getLatestTotalUangMasuk(kavlingKode: String): Flow<Result<Long>>
}