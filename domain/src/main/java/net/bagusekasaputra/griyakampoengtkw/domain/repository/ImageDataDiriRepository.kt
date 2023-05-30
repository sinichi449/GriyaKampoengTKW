package net.bagusekasaputra.griyakampoengtkw.domain.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ImageDataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.ImageDataDiriUri

interface ImageDataDiriRepository {

    fun getByKavlingKode(kavlingKode: String): Flow<Result<ImageDataDiri?>>

    fun getBatchUri(listKavling: List<String>): Flow<Result<List<ImageDataDiriUri>?>>

    fun getFromBackup(kavlingKode: String): Flow<Result<ImageDataDiri?>>

    fun addImage(kavlingKode: String, uri: Uri): Flow<Result<Boolean?>>

    fun updateImage(oldImageDataDiri: ImageDataDiri, newImageDataDiri: ImageDataDiri): Flow<Result<Boolean>>

    fun deleteImage(imageDataDiri: ImageDataDiri): Flow<Result<Boolean>>

    fun getUriByKavlingKode(kavlingKode: String): Flow<Result<Uri>>


    /**
     * Inden Booking related
     */
    suspend fun getFromIndenBooking(keyId: String): Result<Uri?>

    suspend fun insertFromIndenBooking(keyId: String, uri: Uri): Result<Nothing?>
}