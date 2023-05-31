package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

interface LocalIndenBookingDataSource {

    suspend fun getAllKeyIds(): Result<List<String>?>

}