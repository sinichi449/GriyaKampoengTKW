package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.tambahanPembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.TambahanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.TambahanPembayaranRepository

class UpdateTambahanPembayaranAsyncUseCase(
    private val tambahanPembayaranRepository: TambahanPembayaranRepository
): AsyncUseCase<UpdateTambahanPembayaranAsyncUseCase.Request, Nothing>() {

    data class Request(
        val oldData: TambahanPembayaran,
        val newData: TambahanPembayaran,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Nothing?>> {
        return flow {
            // Check equality
            if (request.oldData == request.newData) {
                emit(Result.failure(Exception("Data tidak ada yang berubah!")))
            } else if (request.oldData.id != request.newData.id) {
                emit(Result.failure(Exception("ID tidak valid!")))
            } else {
                emit(tambahanPembayaranRepository.update(
                    request.oldData.kavling,
                    request.oldData.id,
                    request.newData
                ))
            }
        }
    }
}