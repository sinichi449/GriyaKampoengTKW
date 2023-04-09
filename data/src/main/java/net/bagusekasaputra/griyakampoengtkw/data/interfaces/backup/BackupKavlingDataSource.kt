package net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup

import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingModel

interface BackupKavlingDataSource {

    suspend fun getKavlingByBlockKode(blockKode: String): Result<List<KavlingModel>?>

}