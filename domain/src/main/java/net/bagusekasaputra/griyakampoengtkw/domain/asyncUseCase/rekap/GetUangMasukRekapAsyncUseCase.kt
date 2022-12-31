package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.RekapUangMasuk
import net.bagusekasaputra.griyakampoengtkw.domain.repository.RekapUangMasukRepository

class GetUangMasukRekapAsyncUseCase(
    private val rekapUangMasukRepository: RekapUangMasukRepository
): AsyncUseCase<GetUangMasukRekapAsyncUseCase.Request, List<RekapUangMasuk>>() {

    object Request : AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<RekapUangMasuk>?>> {
        return rekapUangMasukRepository.getAll()
    }
}