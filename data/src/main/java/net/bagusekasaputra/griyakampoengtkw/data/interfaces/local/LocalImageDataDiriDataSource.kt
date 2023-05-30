package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriModel

interface LocalImageDataDiriDataSource {

    fun getByKavlingKode(kavlingKode: String): Flow<ImageDataDiriModel?>

    suspend fun insert(
        imageDataDiriModel: ImageDataDiriModel,
        fromRemote: Boolean,
        onSuccess: () -> Unit,
        onFailure: (cause: Throwable?) -> Unit
    )

    suspend fun delete(imageDataDiriModel: ImageDataDiriModel): Result<Nothing?>

    suspend fun deleteByKavlingKode(kavlingKode: String): Result<Nothing?>

    suspend fun update(
        oldModel: ImageDataDiriModel,
        newModel: ImageDataDiriModel,
        onSuccess: () -> Unit,
        onFailure: (cause: Throwable?) -> Unit
    )

    suspend fun getUriByKavlingKode(
        kavlingKode: String,
        onSuccess: (uri: Uri) -> Unit,
        onFailure: (cause: Throwable?) -> Unit,
    )

    fun deleteAll()


    /**
     * Inden Booking related
     */
    suspend fun getFromIndenBooking(keyId: String): Result<Uri?>

    suspend fun insertFromIndenBooking(keyId: String, uri: Uri): Result<Nothing?>

    suspend fun updateFromIndenBooking(keyId: String, newUri: Uri): Result<Nothing?>

    suspend fun deleteFromIndenBooking(keyId: String): Result<Nothing?>

    suspend fun deleteAllFromIndenBooking(): Result<Nothing?>
}