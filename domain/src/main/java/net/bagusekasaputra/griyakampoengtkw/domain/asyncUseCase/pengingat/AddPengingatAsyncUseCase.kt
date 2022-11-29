package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pengingat

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pengingat
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PengingatRepository

class AddPengingatAsyncUseCase(
    private val pengingatRepository: PengingatRepository,
): AsyncUseCase<AddPengingatAsyncUseCase.Request, Long?>() {

    data class Request(val pengingat: Pengingat): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Long?>> {
        return pengingatRepository.insert(request.pengingat)
    }
}