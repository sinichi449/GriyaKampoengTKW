package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoPembayaran

interface FotoPembayaranRepository {

    fun getFotoPembayaran(kavlingKode: String, termin: String): Flow<Result<FotoPembayaran?>>

    fun addFotoPembayaran(
        kavlingKode: String,
        termin: String,
        fotoPembayaran: FotoPembayaran
    ): Flow<Result<Nothing?>>

    fun deleteFotoPembayaran(
        kavlingKode: String,
        termin: String
    ): Flow<Result<Nothing?>>

    // This is to prevent an unintentional replacement of existing image, the data is
    // important after all ...
    fun isFotoPembayaranExist(kavlingKode: String, termin: String): Flow<Result<Boolean>>
}