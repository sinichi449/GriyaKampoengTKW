package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.CatatanPembayaran

interface CatatanPembayaranRepository {

    fun getCatatan(kavlingKode: String, offline: Boolean): Flow<Result<CatatanPembayaran?>>

    fun addCatatan(kavlingKode: String, catatanPembayaran: CatatanPembayaran): Flow<Result<Nothing?>>

    fun deleteCatatan(kavlingKode: String): Flow<Result<Nothing?>>
}