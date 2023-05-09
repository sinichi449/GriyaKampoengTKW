package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingModel

interface RemoteKavlingDataSource {

    suspend fun getAllKavlings(blockKode: String): Result<List<KavlingModel>?>

    suspend fun addKavling(blockKode: String, kavlingModel: KavlingModel): Result<Nothing?>

    suspend fun updateKavling(blockKode: String, oldKavling: KavlingModel, newKavling: KavlingModel): Result<Nothing?>

    suspend fun deleteKavling(blockKode: String, kavlingKode: String): Result<Nothing?>

    suspend fun setKavlingBelumDiisi(kavlingKode: String, belumIsi: Boolean)

    suspend fun getUnmigratedKavlings(backupName: String): Result<List<String>?>
}