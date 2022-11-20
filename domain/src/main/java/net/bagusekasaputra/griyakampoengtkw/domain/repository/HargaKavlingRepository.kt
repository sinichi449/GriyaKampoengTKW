package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling

interface HargaKavlingRepository {

    fun getHargaKavling(kavlingKode: String, offline: Boolean): Flow<Result<HargaKavling?>>

    fun getSingleHargaKavlingForPembayaran(kavlingKode: String): Flow<HargaKavling>

    fun addHargaKavling(hargaKavling: HargaKavling): Flow<Result<Boolean>>

    fun deleteHargaKavling(kavlingKode: String): Flow<Result<Nothing?>>
}