package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingModel

interface LocalKavlingDataSource {

    fun getAsFlow(blok: String): Flow<Result<KavlingModel?>>

    suspend fun getKavlingByBlockKode(blockKode: String): Result<List<KavlingModel>?>

    suspend fun addKavling(blockKode: String, kavlingModel: KavlingModel): Result<Nothing?>

    suspend fun addAll(kavlingModels: List<KavlingModel>): Result<Nothing?>

    suspend fun deleteKavling(kavlingKode: String): Result<Nothing?>

    suspend fun deleteAll(): Result<Nothing?>
}