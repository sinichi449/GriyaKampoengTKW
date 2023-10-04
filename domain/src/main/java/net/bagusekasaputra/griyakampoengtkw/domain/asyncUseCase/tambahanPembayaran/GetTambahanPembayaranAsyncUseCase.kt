package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.tambahanPembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.TambahanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoTambahanPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.TambahanPembayaranRepository

class GetTambahanPembayaranAsyncUseCase(
    private val tambahanPembayaranRepository: TambahanPembayaranRepository,
    private val fotoRepository: FotoTambahanPembayaranRepository,
) : AsyncUseCase<GetTambahanPembayaranAsyncUseCase.Request, List<TambahanPembayaran>>() {

    data class Request(
        val kavling: String
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<TambahanPembayaran>?>> {
        return flow {
            val result = tambahanPembayaranRepository.getAllByKavling(request.kavling)
                .getOrThrow()
            if (!result.isNullOrEmpty()) {
                val masked = TambahanPembayaran.mask(
                    listTambahan = result,
                    onCekFoto = { kavling, id ->
                        fotoRepository.isFotoExists(kavling, id).getOrThrow()
                    }
                )

                emit(Result.success(masked))
            } else {
                emit(Result.success(null))
            }
        }
    }


}