package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.dataDiri

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository

class GetDataDiriAsyncUseCase(
    private val dataDiriRepository: DataDiriRepository,
): AsyncUseCase<GetDataDiriAsyncUseCase.Request, DataDiri?>() {

    data class Request(val kavlingKode: String, val offline: Boolean): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<DataDiri?>> {
        return dataDiriRepository.getDataDiri(request.kavlingKode, request.offline)
    }
}