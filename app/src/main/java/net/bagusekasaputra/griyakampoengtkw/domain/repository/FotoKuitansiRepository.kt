package net.bagusekasaputra.griyakampoengtkw.domain.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoKuitansi

interface FotoKuitansiRepository {

    fun getFoto(kavlingKode: String): Flow<Result<FotoKuitansi?>>

    fun addFoto(kavlingKode: String, dstUri: Uri): Flow<Result<Nothing?>>

}