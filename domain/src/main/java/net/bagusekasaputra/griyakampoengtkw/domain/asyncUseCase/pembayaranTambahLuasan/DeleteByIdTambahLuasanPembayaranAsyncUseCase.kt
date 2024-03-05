package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaranTambahLuasan

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoTambahLuasanRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranTambahLuasanRepository

class DeleteByIdTambahLuasanPembayaranAsyncUseCase(
    private val dataRepository: PembayaranTambahLuasanRepository,
    private val fotoRepository: FotoTambahLuasanRepository,
): AsyncUseCase<DeleteByIdTambahLuasanPembayaranAsyncUseCase.Request, Nothing?>() {

    data class Request(
        val kavling: String,
        val id: String,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return flow {
            dataRepository.delete(request.kavling, request.id)
                .first()
                .getOrThrow()

            val isExistFoto = fotoRepository.isExist(request.kavling, request.id)
                .first()
                .getOrNull() ?: false
            Log.d("DEBUG_ME_PRO", "Foto exist: $isExistFoto")
            if (isExistFoto) {
                fotoRepository.delete(request.kavling, request.id)
                    .first().getOrThrow()
            }

            emit(Result.success(null))
        }
    }
}