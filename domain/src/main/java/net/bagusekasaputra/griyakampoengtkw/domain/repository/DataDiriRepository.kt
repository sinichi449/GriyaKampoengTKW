package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri

interface DataDiriRepository {

    fun getBatchOnline(listKavling: List<String>): Flow<Result<Map<String, DataDiri?>?>>

    fun getBatchBackup(listKavling: List<String>): Flow<Result<Map<String, DataDiri?>?>>

    fun getDataDiri(kavlingKode: String, dataMode: DataMode): Flow<Result<DataDiri?>>

    fun addDataDiri(kavlingKode: String, dataDiri: DataDiri): Flow<Result<Boolean>>

    fun deleteDataDiri(kavlingKode: String): Flow<Result<Boolean>>
}