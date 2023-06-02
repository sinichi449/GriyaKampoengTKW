package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.ambilKuitansi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.AmbilKuitansi
import net.bagusekasaputra.griyakampoengtkw.domain.entity.IndenBookingAmbilKuitansi
import net.bagusekasaputra.griyakampoengtkw.domain.entity.StandardAmbilKuitansi
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingAmbilKuitansiRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.StandardAmbilKuitansiRepository

class InsertAmbilKuitansiAsyncUseCase(
    private val standardAmbilKuitansiRepository: StandardAmbilKuitansiRepository,
    private val indenBookingAmbilKuitansiRepository: IndenBookingAmbilKuitansiRepository,
): AsyncUseCase<InsertAmbilKuitansiAsyncUseCase.Request, Nothing>() {

    sealed class Request(
        val typeAmbilKuitansi: Int,
    ): AsyncUseCase.Request

    data class StandardRequest(
        val ambilKuitansi: StandardAmbilKuitansi
    ): Request(AmbilKuitansi.STANDARD)

    data class IndenBookingRequest(
        val ambilKuitansi: IndenBookingAmbilKuitansi
    ): Request(AmbilKuitansi.INDEN_BOOKING)

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return flow {
            when (request.typeAmbilKuitansi) {
                AmbilKuitansi.STANDARD -> {
                    val standardRequest = request as StandardRequest

                    emit(standardAmbilKuitansiRepository.insert(standardRequest.ambilKuitansi))
                }
                AmbilKuitansi.INDEN_BOOKING -> TODO("Not yet implemented")
            }
        }
    }
}