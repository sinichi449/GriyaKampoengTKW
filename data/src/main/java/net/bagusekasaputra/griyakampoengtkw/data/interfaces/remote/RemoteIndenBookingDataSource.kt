package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import android.net.Uri
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaRumahModel

interface RemoteIndenBookingDataSource {

    suspend fun getAllKeyIds(): Result<List<String>?>

    suspend fun getHargaRumah(keyId: String): Result<HargaRumahModel?>

    suspend fun getFotoIdentitas(keyId: String): Result<Uri?>
}