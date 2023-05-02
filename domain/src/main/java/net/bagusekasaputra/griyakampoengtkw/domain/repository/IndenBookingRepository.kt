package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.IndenBooking

interface IndenBookingRepository {

    fun getAll(): Flow<Result<List<IndenBooking>?>>

}