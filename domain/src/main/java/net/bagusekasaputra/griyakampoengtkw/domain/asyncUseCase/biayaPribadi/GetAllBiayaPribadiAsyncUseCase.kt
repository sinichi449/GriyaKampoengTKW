package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaPribadi

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaPribadi
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaPribadiRepository

class GetAllBiayaPribadiAsyncUseCase(
    private val biayaPribadiRepository: BiayaPribadiRepository
): AsyncUseCase<GetAllBiayaPribadiAsyncUseCase.Request, List<BiayaPribadi>>() {

    object Request: AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<BiayaPribadi>?>> {
        return biayaPribadiRepository.getAll()
    }

}