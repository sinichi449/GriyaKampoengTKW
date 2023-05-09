package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.FeeMarketingModel

interface RemoteFeeMarketingDataSource {

    suspend fun getByKavlingKode(kavlingKode: String): Result<FeeMarketingModel?>

    suspend fun getFromBackup(backupName: String, kavlingKode: String): Result<FeeMarketingModel?>

    suspend fun addFeeMarketing(kavlingKode: String, feeMarketingModel: FeeMarketingModel): Result<Nothing?>

    suspend fun updateFeeMarketing(
        kavlingKode: String,
        oldFeeMarketingModel: FeeMarketingModel,
        newFeeMarketingModel: FeeMarketingModel
    ): Result<Nothing?>

    suspend fun deleteFeeMarketing(kavlingKode: String): Result<Nothing?>
}