package net.bagusekasaputra.griyakampoengtkw.domain.usecase.datadiri

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase

class GetDataDiriUseCase(
    private val dataDiriRepository: DataDiriRepository
): UseCase<GetDataDiriUseCase.Request, GetDataDiriUseCase.Response>() {

    data class Request(val kavlingKode: String): UseCase.Request

    data class Response(val dataDiri: Result<DataDiri?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return dataDiriRepository.getDataDiri(request.kavlingKode).map {
            Response(it)
        }
    }
}