package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteDatabaseUserDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DatabaseUser
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DatabaseUserRepository

class DatabaseUserRepositoryImpl(
    private val remoteRepo: RemoteDatabaseUserDataSource,
): DatabaseUserRepository {

    override fun getAll(): Flow<Result<List<DatabaseUser>?>> {
        return flow {
            emit(Result.success(null))
        }
    }

    override fun insert(databaseUser: DatabaseUser): Flow<Result<Nothing?>> {
        return flow {
            val remoteResult = remoteRepo.insert(
                MyObjectMapper.mapDatabaseUser(databaseUser)
            )

            emit(remoteResult)
        }
    }
}