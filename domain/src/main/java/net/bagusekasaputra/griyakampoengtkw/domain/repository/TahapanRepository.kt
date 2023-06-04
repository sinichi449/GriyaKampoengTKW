package net.bagusekasaputra.griyakampoengtkw.domain.repository

import net.bagusekasaputra.griyakampoengtkw.domain.entity.Tahapan

interface TahapanRepository {

    suspend fun getAllTahapan(): Result<List<Tahapan>?>

}