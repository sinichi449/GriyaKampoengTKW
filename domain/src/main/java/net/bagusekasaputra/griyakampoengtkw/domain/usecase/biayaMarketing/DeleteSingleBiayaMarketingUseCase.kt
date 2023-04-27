package net.bagusekasaputra.griyakampoengtkw.domain.usecase.biayaMarketing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMarketingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase

class DeleteSingleBiayaMarketingUseCase(
    private val biayaMarketingRepository: BiayaMarketingRepository,
): UseCase<DeleteSingleBiayaMarketingUseCase.Request, DeleteSingleBiayaMarketingUseCase.Response>() {

    data class Request(val kavlingKode: String, val biayaMarketing: BiayaMarketing): UseCase.Request

    data class Response(val result: Result<Nothing?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return biayaMarketingRepository.deleteSingle(
            request.kavlingKode,
            request.biayaMarketing,
        ).map {
            Response(it)
        }
    }
}