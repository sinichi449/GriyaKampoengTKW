package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pengingat

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pengingat
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PengingatRepository

class GetAllPengingatAsyncUseCase(
    private val pengingatRepository: PengingatRepository,
): AsyncUseCase<GetAllPengingatAsyncUseCase.Request, List<Pengingat>?>() {

    object Request: AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<Pengingat>?>> {
        return pengingatRepository.getAll()
    }
}