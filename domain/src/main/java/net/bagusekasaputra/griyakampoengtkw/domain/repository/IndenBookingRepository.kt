package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.IndenBooking

interface IndenBookingRepository {

    fun getAll(dataMode: DataMode): Flow<Result<List<IndenBooking>?>>

    fun insert(indenBooking: IndenBooking): Flow<Result<Nothing?>>

    fun delete(indenBooking: IndenBooking): Flow<Result<Nothing?>>
}