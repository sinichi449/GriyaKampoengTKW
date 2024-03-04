package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoTambahLuasan

interface FotoTambahLuasanRepository {

    fun get(kavling: String, tambahLuasanId: String): Flow<Result<FotoTambahLuasan?>>

}