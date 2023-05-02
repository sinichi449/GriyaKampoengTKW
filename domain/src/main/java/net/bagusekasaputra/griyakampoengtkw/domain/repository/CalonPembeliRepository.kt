package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.CalonPembeli

interface CalonPembeliRepository {

    fun getAll(): Flow<Result<List<CalonPembeli>?>>

}