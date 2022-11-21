package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaMarketingModel

interface LocalBiayaMarketingDataSource {

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

    suspend fun deleteSingle(kavlingKode: String, timeMillis: Long): Result<Nothing?>

    suspend fun deleteAllBiayaMarketing(kavlingKode: String): Result<Nothing?>

}