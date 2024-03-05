package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoTambahLuasan

interface FotoTambahLuasanRepository {

    fun get(kavling: String, tambahLuasanId: String): Flow<Result<FotoTambahLuasan?>>

    fun insert(entity: FotoTambahLuasan): Flow<Result<Nothing?>>

    fun delete(kavling: String, tambahLuasanId: String): Flow<Result<Nothing?>>

    fun isExist(kavling: String, tambahLuasanId: String): Flow<Result<Boolean>>
}