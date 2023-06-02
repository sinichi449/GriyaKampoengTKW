package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.pembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaRumahIndenBookingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoPembayaranIndenBookingRepository

class GetAllPembayaranIndenBookingAsyncUseCase(
    private val hargaRumahIndenBookingRepository: HargaRumahIndenBookingRepository,
    private val pembayaranRepository: PembayaranRepository,
    private val fotoPembayaranRepository: FotoPembayaranIndenBookingRepository,
): AsyncUseCase<GetAllPembayaranIndenBookingAsyncUseCase.Request, List<Pembayaran>>() {

    data class Request(val keyId: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<Pembayaran>?>> {
        return flow {
            val pembayaranList = pembayaranRepository
                .getAllFromIndenBooking(request.keyId)
                .getOrThrow()
            val hargaRumah = hargaRumahIndenBookingRepository.get(request.keyId, DataMode.ONLINE)
                .getOrThrow()


            if ((hargaRumah != null && !pembayaranList.isNullOrEmpty())) {
                val maskedPembayaran = Pembayaran.maskPembayaran(pembayaranList, hargaRumah,
                    onCekFotoPembayaran = { termin ->
                        fotoPembayaranRepository.isExist(request.keyId, termin).getOrThrow()
                    },
                    onCekSudahAmbilKuitansi = { _ ->
                        // TODO
                        false
                    }
                )

                emit(Result.success(maskedPembayaran))
            } else {
                emit(Result.success(null))
            }
        }
    }
}