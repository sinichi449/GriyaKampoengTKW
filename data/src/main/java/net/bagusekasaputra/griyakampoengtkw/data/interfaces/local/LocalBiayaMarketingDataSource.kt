package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaMarketingModel

interface LocalBiayaMarketingDataSource {

    suspend fun getAllBiayaMarketing(kavlingKode: String): Result<Map<Long, BiayaMarketingModel>?>

    suspend fun addBiayaMarketing(
        kavlingKode: String,
        biayaMarketingModel: BiayaMarketingModel
    ): Result<Nothing?>

    suspend fun update(
        id: Long,
        newBiayaMarketingModel: BiayaMarketingModel
    ): Result<Nothing?>

    suspend fun deleteSingle(id: Long): Result<Nothing?>

    suspend fun deleteAllBiayaMarketing(kavlingKode: String): Result<Nothing?>

}