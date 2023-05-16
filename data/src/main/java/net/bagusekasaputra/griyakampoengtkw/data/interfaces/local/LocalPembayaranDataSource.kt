package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel

interface LocalPembayaranDataSource {

    suspend fun getAllPembayaran(kavlingKode: String): Result<List<PembayaranModel>?>

    suspend fun addPembayaranModel(kavlingKode: String, hargaKavling: Long, pembayaranModel: PembayaranModel): Result<Nothing?>

    suspend fun addAllPembayaranModel(kavlingKode: String, models: List<PembayaranModel>): Result<Nothing?>

    suspend fun updatePembayaranModel(kavlingKode: String, oldPembayaranModel: PembayaranModel, newPembayaranModel: PembayaranModel): Result<Nothing?>

    suspend fun deletePembayaranModelByTermin(kavlingKode: String, termin: String): Result<Nothing?>

    suspend fun deleteAllPembayaranModel(kavlingKode: String): Result<Nothing?>

    suspend fun deleteAll(): Result<Nothing?>
}