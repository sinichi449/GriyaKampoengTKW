package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.PembayaranBulanan
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BaselinePembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository

class GetListPembayaranBulananAsyncUseCase(
    private val pembayaranRepository: PembayaranRepository,
    private val baselinePembayaranRepository: BaselinePembayaranRepository,
    private val hargaKavlingRepository: HargaKavlingRepository,
    private val fotoPembayaranRepository: FotoPembayaranRepository,
): AsyncUseCase<GetListPembayaranBulananAsyncUseCase.Request, List<PembayaranBulanan>>() {

    data class Request(val kavlingKode: String, val dataMode: DataMode): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<PembayaranBulanan>?>> {
        return flow {
            val pembayarans = pembayaranRepository
                .getAllPembayaran(request.kavlingKode, request.dataMode)
                .first().getOrThrow()
            val baseline = baselinePembayaranRepository.get(request.kavlingKode, request.dataMode)
                .first().getOrThrow()
            val hargaKavling = hargaKavlingRepository.getHargaKavling(request.kavlingKode, request.dataMode)
                .first().getOrThrow()

            if (baseline != null && hargaKavling != null && pembayarans != null) {
                val maskedPembayaran = Pembayaran.maskPembayaran(pembayarans, hargaKavling,
                    onCekFotoPembayaran = { termin ->
                        val sudahIsiFotoPembayaran = fotoPembayaranRepository
                            .isFotoPembayaranExist(request.kavlingKode, termin, request.dataMode)
                            .first()
                            .getOrThrow()

                        sudahIsiFotoPembayaran
                    }
                )
                val listPembayaranBulanan = PembayaranBulanan
                    .groupPembayaranIntoBulanan(request.kavlingKode, baseline, maskedPembayaran)
                val maskedPembayaranBulanans = PembayaranBulanan.mask(listPembayaranBulanan)

                emit(Result.success(maskedPembayaranBulanans))
            } else {
                emit(Result.success(null))
            }
        }
    }
}