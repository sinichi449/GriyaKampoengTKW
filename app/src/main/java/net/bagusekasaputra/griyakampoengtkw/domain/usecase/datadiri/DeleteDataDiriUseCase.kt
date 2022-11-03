package net.bagusekasaputra.griyakampoengtkw.domain.usecase.datadiri

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteDataDiriUseCase @Inject constructor(
    private val dataDiriRepository: DataDiriRepository
): UseCase<DeleteDataDiriUseCase.Request, DeleteDataDiriUseCase.Response>() {

    data class Request(val kavlingKode: String): UseCase.Request

    data class Response(val result: Result<Boolean>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return dataDiriRepository.deleteDataDiri(request.kavlingKode).map {
            Response(it)
        }
    }
}