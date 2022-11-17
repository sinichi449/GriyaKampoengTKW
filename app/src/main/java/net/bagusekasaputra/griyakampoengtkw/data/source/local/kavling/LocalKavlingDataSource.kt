package net.bagusekasaputra.griyakampoengtkw.data.source.local.kavling

import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingModel

interface LocalKavlingDataSource {

    suspend fun getKavlingByBlockKode(blockKode: String): Result<List<KavlingModel>?>

    suspend fun addKavling(blockKode: String, kavlingModel: KavlingModel): Result<Nothing?>

    suspend fun deleteKavling(kavlingKode: String): Result<Nothing?>
}