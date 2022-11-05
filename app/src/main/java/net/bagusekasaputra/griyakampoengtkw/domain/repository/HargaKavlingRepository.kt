package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling

interface HargaKavlingRepository {

    fun getHargaKavling(kavlingKode: String): Flow<Result<HargaKavling?>>

    fun getSingleHargaKavlingForPembayaran(kavlingKode: String): Flow<Long> 

    fun addHargaKavling(hargaKavling: HargaKavling): Flow<Result<Boolean>>

}