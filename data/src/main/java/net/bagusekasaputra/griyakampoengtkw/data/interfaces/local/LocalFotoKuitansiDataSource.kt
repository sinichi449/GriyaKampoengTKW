package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.FotoKuitansiModel

interface LocalFotoKuitansiDataSource {

    suspend fun getFotoKuitansi(kavlingKode: String): Result<FotoKuitansiModel?>

    suspend fun addFotoKuitansi(fotoKuitansiModel: FotoKuitansiModel): Result<Nothing?>

}