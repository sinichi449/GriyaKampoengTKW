package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.MetadataModel

interface LocalMetadataDataSource {

    fun get(tableName: String): MetadataModel?

    suspend fun insert(model: MetadataModel)

}