package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling

interface HargaKavlingRepository {

    fun getBatchOnline(listKavling: List<String>): Flow<Result<Map<String, HargaKavling?>?>>

    fun getBatchBackup(listKavling: List<String>): Flow<Result<Map<String, HargaKavling?>?>>

    fun getHargaKavling(kavlingKode: String, dataMode: DataMode): Flow<Result<HargaKavling?>>

    fun getSingleHargaKavlingForPembayaran(kavlingKode: String): Flow<HargaKavling>

    fun addHargaKavling(hargaKavling: HargaKavling): Flow<Result<Boolean>>

    fun deleteHargaKavling(kavlingKode: String): Flow<Result<Nothing?>>
}