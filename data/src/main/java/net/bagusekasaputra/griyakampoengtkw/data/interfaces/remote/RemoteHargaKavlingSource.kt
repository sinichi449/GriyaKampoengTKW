package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.HargaKavlingModel

interface RemoteHargaKavlingSource {

    suspend fun getHargaKavlingModel(kavlingKode: String): Result<HargaKavlingModel?>

    suspend fun getFromBackup(backupName: String, kavlingKode: String): Result<HargaKavlingModel?>

    suspend fun addHargaKavlingModel(hargaKavlingModel: HargaKavlingModel): Result<Nothing?>

    suspend fun deleteHargaKavlingModel(kavlingKode: String): Result<Nothing?>

    fun getSingleHargaKavlingForPembayaran(kavlingKode: String, onSuccess: (hargaKavlingModel: HargaKavlingModel?) -> Unit)
}