package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import android.net.Uri
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriModel

interface LocalImageDataDiriDataSource {

    suspend fun getByKavlingKode(
        kavlingKode: String,
        onSuccess: (imageDataDiriModel: ImageDataDiriModel) -> Unit,
        onFailure: (cause: Throwable?) -> Unit
    )

    suspend fun insert(
        imageDataDiriModel: ImageDataDiriModel,
        onSuccess: () -> Unit,
        onFailure: (cause: Throwable?) -> Unit
    )

    suspend fun delete(
        imageDataDiriModel: ImageDataDiriModel,
        onSuccess: () -> Unit,
        onFailure: (cause: Throwable?) -> Unit
    )

    suspend fun deleteByKavlingKode(
        kavlingKode: String,
        onSuccess: () -> Unit,
        onFailure: (cause: Throwable?) -> Unit,
    )

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
}