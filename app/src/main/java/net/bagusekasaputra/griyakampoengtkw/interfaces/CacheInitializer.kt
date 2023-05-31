package net.bagusekasaputra.griyakampoengtkw.interfaces

interface CacheInitializer {

    suspend fun initialize(): Result<Nothing?>

    fun isUnitialized(): Boolean

}