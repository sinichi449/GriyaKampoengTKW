package net.bagusekasaputra.griyakampoengtkw.domain.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ImageDataDiri

interface ImageDataDiriRepository {

    fun getByKavlingKode(kavlingKode: String): Flow<Result<ImageDataDiri>>

    fun addImage(kavlingKode: String, uri: Uri): Flow<Result<Boolean>>

    fun updateImage(oldImageDataDiri: ImageDataDiri, newImageDataDiri: ImageDataDiri): Flow<Result<Boolean>>

    fun deleteImage(imageDataDiri: ImageDataDiri): Flow<Result<Boolean>>

    fun getUriByKavlingKode(kavlingKode: String): Flow<Result<Uri>>

}