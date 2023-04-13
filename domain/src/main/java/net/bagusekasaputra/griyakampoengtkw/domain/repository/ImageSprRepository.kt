package net.bagusekasaputra.griyakampoengtkw.domain.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ImageSpr

interface ImageSprRepository {

    fun getByKavlingKode(kavlingKode: String): Flow<Result<ImageSpr?>>

    fun getFromBackup(kavlingKode: String): Flow<Result<ImageSpr?>>

    fun addImage(kavlingKode: String, uri: Uri): Flow<Result<Nothing?>>

}