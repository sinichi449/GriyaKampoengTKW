package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pengingat

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pengingat
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PengingatRepository

class DeletePengingatAsyncUseCase(
    private val pengingatRepository: PengingatRepository,
): AsyncUseCase<DeletePengingatAsyncUseCase.Request, Nothing?>() {

    data class Request(val oldPengingat: Pengingat): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return pengingatRepository.delete(request.oldPengingat)
    }
}