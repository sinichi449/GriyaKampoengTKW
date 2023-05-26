package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import android.net.Uri
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaRumahModel

interface LocalIndenBookingDataSource {

    suspend fun getAllKeyIds(): Result<List<String>?>

    suspend fun getHargaRumah(keyId: String): Result<HargaRumahModel?>

    suspend fun getFotoIdentitas(keyId: String): Result<Uri?>

    suspend fun insertFotoIdentitas(keyId: String, uri: Uri): Result<Nothing?>

    suspend fun invalidateFotoIdentitas(): Result<Nothing?>
}