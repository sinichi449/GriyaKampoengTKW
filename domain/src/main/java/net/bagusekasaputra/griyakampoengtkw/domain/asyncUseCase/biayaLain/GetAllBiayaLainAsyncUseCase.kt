package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaLain

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaLainRepository

class GetAllBiayaLainAsyncUseCase(
    private val biayaLainRepository: BiayaLainRepository,
): AsyncUseCase<GetAllBiayaLainAsyncUseCase.Request, List<BiayaLain>?>() {

    data class Request(val dataMode: DataMode): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<BiayaLain>?>> {
        return if (request.dataMode != DataMode.DATA_LAMA) {
            biayaLainRepository.getAllOnline(request.dataMode)
        } else {
            biayaLainRepository.getFromBackup()
        }
    }
}