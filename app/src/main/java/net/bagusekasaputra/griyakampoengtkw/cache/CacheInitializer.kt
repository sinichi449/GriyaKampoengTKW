package net.bagusekasaputra.griyakampoengtkw.cache

import net.bagusekasaputra.griyakampoengtkw.model.Tahapan

interface CacheInitializer {

    suspend fun initialize(tahapan: Tahapan): Result<Nothing?>

    fun isUnitialized(): Boolean

}