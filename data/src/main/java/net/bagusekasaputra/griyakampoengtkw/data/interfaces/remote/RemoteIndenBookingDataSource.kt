package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

interface RemoteIndenBookingDataSource {

    suspend fun getAllKeyIds(): Result<List<String>?>

}