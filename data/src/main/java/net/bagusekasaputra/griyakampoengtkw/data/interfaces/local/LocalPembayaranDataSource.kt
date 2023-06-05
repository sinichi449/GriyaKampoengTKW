package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel

interface LocalPembayaranDataSource {

    suspend fun getByKavlingAndTermin(kavlingKode: String, termin: String): Result<PembayaranModel?>

    suspend fun getAllPembayaran(kavlingKode: String): Result<List<PembayaranModel>?>

    suspend fun addPembayaranModel(kavlingKode: String, pembayaranModel: PembayaranModel): Result<Nothing?>

    suspend fun addAllPembayaranModel(kavlingKode: String, models: List<PembayaranModel>): Result<Nothing?>

    suspend fun update(
        kavlingKode: String,
        termin: String,
        newModel: PembayaranModel
    ): Result<Nothing?>

    suspend fun deletePembayaranModelByTermin(kavlingKode: String, termin: String): Result<Nothing?>

    suspend fun deleteAllPembayaranModel(kavlingKode: String): Result<Nothing?>

    suspend fun deleteAll(): Result<Nothing?>


    /**
     * Inden Booking related
     */
    suspend fun getAllFromIndenBooking(keyId: String): Result<List<PembayaranModel>?>

    suspend fun insertFromIndenBooking(keyId: String, model: PembayaranModel): Result<Nothing?>

    suspend fun insertAllFromIndenBooking(keyId: String, models: List<PembayaranModel>)
        : Result<Nothing?>

    suspend fun deleteAllFromIndenBooking(): Result<Nothing?>
}