package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.databaseUser

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DatabaseUser
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DatabaseUserRepository

class InsertDatabaseUserAsyncUseCase(
    private val databaseUserRepository: DatabaseUserRepository,
): AsyncUseCase<InsertDatabaseUserAsyncUseCase.Request, Nothing>() {

    data class Request(val user: DatabaseUser): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return databaseUserRepository.insert(request.user)
    }
}