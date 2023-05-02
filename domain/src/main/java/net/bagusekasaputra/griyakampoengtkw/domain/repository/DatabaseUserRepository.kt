package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DatabaseUser

interface DatabaseUserRepository {

    fun getAll(): Flow<Result<List<DatabaseUser>?>>

}