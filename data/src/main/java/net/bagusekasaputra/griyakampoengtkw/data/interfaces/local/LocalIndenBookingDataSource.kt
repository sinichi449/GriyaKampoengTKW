package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import android.net.Uri
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaRumahModel
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel

interface LocalIndenBookingDataSource {

    suspend fun getAllKeyIds(): Result<List<String>?>

    suspend fun getAllPembayaran(keyId: String): Result<List<PembayaranModel>?>

    suspend fun getHargaRumah(keyId: String): Result<HargaRumahModel?>

    suspend fun getFotoIdentitas(keyId: String): Result<Uri?>

    suspend fun insertAllPembayaran(keyId: String, pembayaranList: List<PembayaranModel>)
        : Result<Nothing?>

    suspend fun insertFotoIdentitas(keyId: String, uri: Uri): Result<Nothing?>

    suspend fun invalidatePembayaran(): Result<Nothing?>

    suspend fun invalidateFotoIdentitas(): Result<Nothing?>
}