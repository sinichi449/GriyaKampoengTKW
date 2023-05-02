package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.MetadataModel

interface RemoteMetadataDataSource {

    suspend fun get(tableName: String): MetadataModel?

    suspend fun update(oldMetadataModel: MetadataModel, newMetadataModel: MetadataModel)

    suspend fun delete(tableName: String)
}