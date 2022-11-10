package net.bagusekasaputra.griyakampoengtkw.data.source.local.kavling

import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingModel

interface LocalKavlingRepository {

    suspend fun getKavlingByBlockKode(blockKode: String): Result<List<KavlingModel>?>

    suspend fun addKavling(blockKode: String, kavlingModel: KavlingModel): Result<Nothing?>

}