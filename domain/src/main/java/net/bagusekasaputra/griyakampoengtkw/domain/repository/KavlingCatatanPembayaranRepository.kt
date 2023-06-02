package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.KavlingCatatanPembayaran

interface KavlingCatatanPembayaranRepository {

    fun getCatatan(kavlingKode: String, dataMode: DataMode): Flow<Result<KavlingCatatanPembayaran?>>

    fun getBatch(listKavling: List<String>): Flow<Result<List<KavlingCatatanPembayaran>?>>

    fun addCatatan(kavlingKode: String, kavlingCatatanPembayaran: KavlingCatatanPembayaran): Flow<Result<Nothing?>>

    fun deleteCatatan(kavlingKode: String): Flow<Result<Nothing?>>
}