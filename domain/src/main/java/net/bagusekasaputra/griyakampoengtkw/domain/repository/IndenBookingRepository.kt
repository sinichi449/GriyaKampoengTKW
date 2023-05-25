package net.bagusekasaputra.griyakampoengtkw.domain.repository

import android.net.Uri
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.HargaRumahIndenBooking

interface IndenBookingRepository {

    suspend fun getAllKeyIds(dataMode: DataMode): Result<List<String>?>

    suspend fun getAllPembayaran(keyId: String, dataMode: DataMode): Result<List<Pembayaran>?>

    suspend fun getHargaRumah(keyId: String, dataMode: DataMode): Result<HargaRumahIndenBooking?>

    suspend fun getFotoIdentitas(keyId: String, dataMode: DataMode)
        : Result<Uri?>

}