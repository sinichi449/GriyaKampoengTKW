package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import android.net.Uri
import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaRumahModel
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel

interface LocalIndenBookingDataSource {

    suspend fun getAllKeyIds(): Result<List<String>?>

    suspend fun getDataDiri(keyId: String): Result<DataDiriModel?>

    suspend fun getAllPembayaran(keyId: String): Result<List<PembayaranModel>?>

    suspend fun getHargaRumah(keyId: String): Result<HargaRumahModel?>

    suspend fun getFotoIdentitas(keyId: String): Result<Uri?>


    suspend fun insertDataDiri(keyId: String, dataDiriModel: DataDiriModel): Result<Nothing?>

    suspend fun insertFotoIdentitas(keyId: String, uri: Uri): Result<Nothing?>


    suspend fun invalidate(keyId: String): Result<Nothing?>
}