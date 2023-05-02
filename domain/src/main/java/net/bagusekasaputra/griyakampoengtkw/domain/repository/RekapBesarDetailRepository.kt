package net.bagusekasaputra.griyakampoengtkw.domain.repository

import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.RekapBesarDetail

interface RekapBesarDetailRepository {

    suspend fun get(): Result<RekapBesarDetail?>

    suspend fun insert(rekapBesarDetail: RekapBesarDetail): Result<Nothing?>

    suspend fun delete(): Result<Nothing?>

}