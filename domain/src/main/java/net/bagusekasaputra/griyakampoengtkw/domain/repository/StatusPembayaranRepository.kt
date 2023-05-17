package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.statusPembayaran.StatusPembayaran

interface StatusPembayaranRepository {

    fun get(kavling: String, dataMode: DataMode): Flow<Result<StatusPembayaran?>>

}