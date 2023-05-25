package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingRepository

class GetAllPembayaranIndenBookingAsyncUseCase(
    private val indenBookingRepository: IndenBookingRepository,
): AsyncUseCase<GetAllPembayaranIndenBookingAsyncUseCase.Request, List<Pembayaran>>() {

    data class Request(val keyId: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<Pembayaran>?>> {
        return flow {
            val pembayaranList = indenBookingRepository
                .getAllPembayaran(request.keyId, DataMode.ONLINE)
                .getOrThrow()
            val hargaRumah = indenBookingRepository.getHargaRumah(request.keyId, DataMode.ONLINE)
                .getOrThrow()

            // Masking pembayaran: Total uang masuk, persentase, etc
            val pseudoHargaKavling = hargaRumah?.toHargaKavling("D99")
            val maskedPembayaran = pembayaranList?.let {
                if (pseudoHargaKavling != null) {
                    Pembayaran.maskPembayaran(it, pseudoHargaKavling,
                        onCekFotoPembayaran = {
                            // TODO: Cek foto pembayaran
                            false
                        }
                    )
                } else {
                    null
                }
            }

            emit(Result.success(maskedPembayaran))
        }
    }
}