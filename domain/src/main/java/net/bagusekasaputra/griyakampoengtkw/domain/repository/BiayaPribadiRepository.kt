package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaPribadi

interface BiayaPribadiRepository {

    fun getAll(): Flow<Result<List<BiayaPribadi>?>>

}