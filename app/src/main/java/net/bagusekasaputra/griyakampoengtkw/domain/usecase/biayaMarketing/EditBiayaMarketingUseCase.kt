package net.bagusekasaputra.griyakampoengtkw.domain.usecase.biayaMarketing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMarketingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditBiayaMarketingUseCase @Inject constructor(
    private val biayaMarketingRepository: BiayaMarketingRepository,
): UseCase<EditBiayaMarketingUseCase.Request, EditBiayaMarketingUseCase.Response>() {

    data class Request(
        val oldBiayaMarketing: BiayaMarketing,
        val newBiayaMarketing: BiayaMarketing
    ): UseCase.Request

    data class Response(val result: Result<Nothing?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return biayaMarketingRepository.update(
            request.oldBiayaMarketing,
            request.newBiayaMarketing,
        ).map {
            Response(it)
        }
    }
}