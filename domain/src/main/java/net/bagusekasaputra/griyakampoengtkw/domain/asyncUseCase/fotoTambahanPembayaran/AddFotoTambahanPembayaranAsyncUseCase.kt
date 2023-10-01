package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoTambahanPembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoTambahanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoTambahanPembayaranRepository

class AddFotoTambahanPembayaranAsyncUseCase(
    private val fotoTambahanPembayaranRepository: FotoTambahanPembayaranRepository
): AsyncUseCase<AddFotoTambahanPembayaranAsyncUseCase.Request, Nothing>() {

    data class Request(
        val kavling: String,
        val id: String,
        val uri: String,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return flow {
            val entity = FotoTambahanPembayaran(
                kavling = request.kavling,
                tambahanPembayaranId = request.id,
                uri = request.uri,
            )

            emit(fotoTambahanPembayaranRepository.insert(entity))
        }
    }
}