package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.FeeMarketingModel

interface LocalFeeMarketingDataSource {

    suspend fun getByKavlingKode(kavlingKode: String): Result<FeeMarketingModel?>

    suspend fun addFeeMarketing(kavlingKode: String, feeMarketingModel: FeeMarketingModel): Result<Nothing?>

    suspend fun updateFeeMarketing(
        kavlingKode: String,
        oldFeeMarketingModel: FeeMarketingModel,
        newFeeMarketingModel: FeeMarketingModel
    ): Result<Nothing?>

    suspend fun deleteFeeMarketing(kavlingKode: String): Result<Nothing?>

    suspend fun deleteAll(): Result<Nothing?>

}