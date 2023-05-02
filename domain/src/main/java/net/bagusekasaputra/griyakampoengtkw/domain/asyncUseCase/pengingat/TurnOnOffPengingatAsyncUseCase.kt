package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pengingat

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pengingat
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PengingatRepository

class TurnOnOffPengingatAsyncUseCase(
    private val pengingatRepository: PengingatRepository,
): AsyncUseCase<TurnOnOffPengingatAsyncUseCase.Request, Nothing?>() {

    data class Request(val pengingat: Pengingat): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        val newPengingat = Pengingat(
            id = request.pengingat.id,
            title = request.pengingat.title,
            content = request.pengingat.content,
            date = request.pengingat.date,
            time = request.pengingat.time,
            isActive = request.pengingat.isActive.not() // Invert the isActive
        )

        return pengingatRepository.update(request.pengingat, newPengingat)
    }
}