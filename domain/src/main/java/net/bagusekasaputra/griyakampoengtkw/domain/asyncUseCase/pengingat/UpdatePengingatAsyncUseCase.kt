package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pengingat

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pengingat
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PengingatRepository

class UpdatePengingatAsyncUseCase(
    private val pengingatRepository: PengingatRepository,
): AsyncUseCase<UpdatePengingatAsyncUseCase.Request, Nothing?>() {

    data class Request(val oldPengingat: Pengingat, val newPengingat: Pengingat): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return pengingatRepository.update(request.oldPengingat, request.newPengingat)
    }
}