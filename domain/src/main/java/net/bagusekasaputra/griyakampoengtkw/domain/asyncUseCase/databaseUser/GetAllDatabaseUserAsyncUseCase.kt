package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.databaseUser

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DatabaseUser
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DatabaseUserRepository

class GetAllDatabaseUserAsyncUseCase(
    private val databaseUserRepository: DatabaseUserRepository
): AsyncUseCase<GetAllDatabaseUserAsyncUseCase.Request, List<DatabaseUser>>() {

    object Request: AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<DatabaseUser>?>> {
        return databaseUserRepository.getAll().map { result ->
            result.map { listCalonPembeli ->
                DatabaseUser.sortbyLastModified(listCalonPembeli)
            }
        }
    }
}