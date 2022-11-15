package net.bagusekasaputra.griyakampoengtkw.data.source.remote.biayaMarketing

import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaMarketingModel

interface RemoteBiayaMarketingDataSource {

    suspend fun getAllBiayaMarketing(kavlingKode: String): Result<List<BiayaMarketingModel>?>

    suspend fun addBiayaMarketing(
        kavlingKode: String,
        biayaMarketingModel: BiayaMarketingModel
    ): Result<Nothing?>

    suspend fun update(
        kavlingKode: String,
        oldBiayaMarketingModel: BiayaMarketingModel,
        newBiayaMarketingModel: BiayaMarketingModel
    ): Result<Nothing?>

    suspend fun deleteSingle(kavlingKode: String, nomor: Int): Result<Nothing?>

    suspend fun deleteAllBiayaMarketing(kavlingKode: String): Result<Nothing?>
}