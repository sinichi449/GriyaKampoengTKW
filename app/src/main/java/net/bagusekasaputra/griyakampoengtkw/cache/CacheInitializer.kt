package net.bagusekasaputra.griyakampoengtkw.cache

import net.bagusekasaputra.griyakampoengtkw.domain.entity.Tahapan

interface CacheInitializer {

    suspend fun initialize(tahapan: Tahapan): Result<Nothing?>

    fun isUnitialized(): Boolean

}