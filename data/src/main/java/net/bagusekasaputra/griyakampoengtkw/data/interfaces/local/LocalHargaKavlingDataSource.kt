package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.HargaKavlingModel

interface LocalHargaKavlingDataSource {

    suspend fun getHargaKavlingModel(kavlingKode: String): Result<HargaKavlingModel?>

    suspend fun addHargaKavlingModel(hargaKavlingModel: HargaKavlingModel): Result<Nothing?>

    suspend fun deleteHargaKavlingModel(kavlingKode: String): Result<Nothing?>

}