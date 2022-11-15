package net.bagusekasaputra.griyakampoengtkw.domain.usecase.biayaMarketing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMarketingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddBiayaMarketingUseCase @Inject constructor(
    private val biayaMarketingRepository: BiayaMarketingRepository,
): UseCase<AddBiayaMarketingUseCase.Request, AddBiayaMarketingUseCase.Response>() {

    data class Request(val biayaMarketing: BiayaMarketing): UseCase.Request

    data class Response(val result: Result<Nothing?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return biayaMarketingRepository.addBiayaMarketing(request.biayaMarketing).map {
            Response(it)
        }
    }

}