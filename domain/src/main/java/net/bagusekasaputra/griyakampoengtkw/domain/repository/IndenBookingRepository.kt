package net.bagusekasaputra.griyakampoengtkw.domain.repository

import net.bagusekasaputra.griyakampoengtkw.domain.DataMode

interface IndenBookingRepository {

    suspend fun getAllKeyIds(dataMode: DataMode): Result<List<String>?>

}