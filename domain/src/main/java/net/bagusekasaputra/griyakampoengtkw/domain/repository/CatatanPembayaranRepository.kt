package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.CatatanPembayaran

interface CatatanPembayaranRepository {

    fun getCatatan(kavlingKode: String, dataMode: DataMode): Flow<Result<CatatanPembayaran?>>

    fun getBatch(listKavling: List<String>): Flow<Result<List<CatatanPembayaran>?>>

    fun addCatatan(kavlingKode: String, catatanPembayaran: CatatanPembayaran): Flow<Result<Nothing?>>

    fun deleteCatatan(kavlingKode: String): Flow<Result<Nothing?>>
}