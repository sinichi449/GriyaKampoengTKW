package net.bagusekasaputra.griyakampoengtkw.domain.usecase.biayaMarketing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMarketingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase

class DeleteAllBiayaMarketingUseCase(
    private val biayaMarketingRepository: BiayaMarketingRepository,
): UseCase<DeleteAllBiayaMarketingUseCase.Request, DeleteAllBiayaMarketingUseCase.Response>() {

    data class Request(val kavlingKode: String): UseCase.Request

    data class Response(val result: Result<Nothing?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return biayaMarketingRepository.deleteAll(request.kavlingKode).map {
            Response(it)
        }
    }
}