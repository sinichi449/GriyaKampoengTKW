package net.bagusekasaputra.griyakampoengtkw.data.interfaces

import net.bagusekasaputra.griyakampoengtkw.data.model.MetadataModel

/**
 * An interface for any data sources which have [MetadataModel].
 *
 * Simplifying timestamp comparison, for example, between any `LocalDataSource` and
 * `RemoteDataSource`.
 */
interface Cacheable {

    fun getTableName(): String

}