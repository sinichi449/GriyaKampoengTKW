package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoPembayaran

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoPembayaranRepository

class AddFotoPembayaranAsyncUseCase(
    private val fotoPembayaranRepository: FotoPembayaranRepository,
): AsyncUseCase<AddFotoPembayaranAsyncUseCase.Request, Nothing?>() {

    data class Request(val kavlingKode: String, val termin: String, val uri: Uri): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        val fotoPembayaran = FotoPembayaran(
            kavlingKode = request.kavlingKode,
            termin = request.termin,
            uri = request.uri,
        )

        return fotoPembayaranRepository.addFotoPembayaran(
            kavlingKode = request.kavlingKode,
            termin = request.termin,
            fotoPembayaran = fotoPembayaran,
        )
    }
}