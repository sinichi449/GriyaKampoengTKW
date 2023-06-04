package net.bagusekasaputra.griyakampoengtkw.repository

import net.bagusekasaputra.griyakampoengtkw.model.Tahapan

interface TahapanRepository {

    suspend fun getAllTahapan(): Result<List<Tahapan>?>

}