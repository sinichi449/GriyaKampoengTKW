package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.IndenBookingModel

interface LocalIndenBookingDataSource {

    suspend fun getAll(): Result<List<IndenBookingModel>?>

    // We need to return the ID, so the return type would be a Long
    suspend fun insert(model: IndenBookingModel): Result<Nothing?>

    suspend fun insertAll(listModel: List<IndenBookingModel>): Result<Nothing?>

    suspend fun delete(model: IndenBookingModel): Result<Nothing?>

    suspend fun deleteAll(): Result<Nothing?>
}