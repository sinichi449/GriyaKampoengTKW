package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.PromotionModel

interface RemotePromotionDataSource {

    suspend fun get(): Result<PromotionModel?>

}