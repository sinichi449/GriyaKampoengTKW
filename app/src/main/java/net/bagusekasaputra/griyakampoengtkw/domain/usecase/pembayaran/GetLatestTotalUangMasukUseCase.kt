package net.bagusekasaputra.griyakampoengtkw.domain.usecase.pembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLatestTotalUangMasukUseCase @Inject constructor(
    private val pembayaranRepository: PembayaranRepository
): UseCase<GetLatestTotalUangMasukUseCase.Request, GetLatestTotalUangMasukUseCase.Response>() {

    data class Request(val kavlingKode: String): UseCase.Request

    data class Response(val result: Result<Long>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return pembayaranRepository.getLatestTotalUangMasuk(request.kavlingKode).map {
            Response(it)
        }
    }
}