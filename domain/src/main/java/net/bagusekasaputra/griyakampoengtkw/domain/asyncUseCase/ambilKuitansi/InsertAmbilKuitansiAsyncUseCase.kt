package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.ambilKuitansi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.AmbilKuitansi
import net.bagusekasaputra.griyakampoengtkw.domain.repository.AmbilKuitansiRepository

class InsertAmbilKuitansiAsyncUseCase(
    private val ambilKuitansiRepository: AmbilKuitansiRepository
): AsyncUseCase<InsertAmbilKuitansiAsyncUseCase.Request, Nothing>() {

    data class Request(val ambilKuitansi: AmbilKuitansi): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return flow {
            emit(ambilKuitansiRepository.insert(request.ambilKuitansi))
        }
    }
}