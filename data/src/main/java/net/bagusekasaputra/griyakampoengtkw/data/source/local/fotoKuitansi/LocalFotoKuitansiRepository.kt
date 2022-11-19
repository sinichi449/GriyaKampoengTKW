package net.bagusekasaputra.griyakampoengtkw.data.source.local.fotoKuitansi

import net.bagusekasaputra.griyakampoengtkw.data.model.FotoKuitansiModel

interface LocalFotoKuitansiRepository {

    suspend fun getFotoKuitansi(kavlingKode: String): Result<FotoKuitansiModel?>

    suspend fun addFotoKuitansi(fotoKuitansiModel: FotoKuitansiModel): Result<Nothing?>

}