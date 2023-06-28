package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.upahPekerja

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.UpahPekerja
import net.bagusekasaputra.griyakampoengtkw.domain.repository.UpahPekerjaRepository

class GetAllUpahPekerjaAsyncUseCase(
    private val upahPekerjaRepository: UpahPekerjaRepository,
): AsyncUseCase<GetAllUpahPekerjaAsyncUseCase.Request, List<UpahPekerja>?>() {

    data class Request(
        val kavling: String,
        val dataMode: DataMode,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<UpahPekerja>?>> {
        return upahPekerjaRepository.getAll(request.kavling, request.dataMode)
    }
}