package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import android.net.Uri
import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaRumahModel
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel

interface RemoteIndenBookingDataSource {

    suspend fun getAllKeyIds(): Result<List<String>?>

    suspend fun getDataDiri(keyId: String): Result<DataDiriModel?>

    suspend fun getAllPembayaran(keyId: String): Result<List<PembayaranModel>?>

    suspend fun getHargaRumah(keyId: String): Result<HargaRumahModel?>

    suspend fun getFotoIdentitas(keyId: String): Result<Uri?>
}