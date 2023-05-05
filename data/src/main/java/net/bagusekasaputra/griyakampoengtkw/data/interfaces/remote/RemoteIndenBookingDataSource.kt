package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.IndenBookingModel

interface RemoteIndenBookingDataSource {

    suspend fun getAll(): Result<List<IndenBookingModel>?>

    suspend fun insert(model: IndenBookingModel): Result<Nothing?>

}