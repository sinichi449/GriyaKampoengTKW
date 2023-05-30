package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriModel

interface RemoteImageDataDiriDataSource {

    suspend fun get(kavlingKode: String): ImageDataDiriModel?

    fun insert(imageDataDiriModel: ImageDataDiriModel): Flow<Result<Boolean?>>

    suspend fun update(oldModel: ImageDataDiriModel, newModel: ImageDataDiriModel)

    suspend fun delete(imageDataDiriModel: ImageDataDiriModel): Result<Nothing?>


    /**
     * Inden Booking related
     */
    suspend fun getFromIndenBooking(keyId: String): Result<Uri?>

    suspend fun insertFromIndenBooking(keyId: String, uri: Uri): Result<Nothing?>

    suspend fun updateFromIndenBooking(keyId: String, newUri: Uri): Result<Nothing?>
}