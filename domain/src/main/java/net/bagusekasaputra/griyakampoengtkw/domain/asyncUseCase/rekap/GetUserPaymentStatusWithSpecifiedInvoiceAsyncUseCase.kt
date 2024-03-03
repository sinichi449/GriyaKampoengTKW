package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.BulanAngsuran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository

class GetUserPaymentStatusWithSpecifiedInvoiceAsyncUseCase(
    private val blockRepository: BlockRepository,
    private val kavlingRepository: KavlingRepository,
    private val pembayaranRepository: PembayaranRepository,
) : AsyncUseCase<GetUserPaymentStatusWithSpecifiedInvoiceAsyncUseCase.Request, List<List<String>>?>() {

    data class Request(
        val bulanAngsuran: BulanAngsuran
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<List<String>>?>> {
        return flow {
            val daftarBloks = blockRepository.getAllBlocks(DataMode.ONLINE).first().getOrThrow()
            val daftarKavling = mutableListOf<Kavling>()
            daftarBloks?.forEach { blok ->
                kavlingRepository.getKavlingByBlock(blok.kode, DataMode.ONLINE).first().getOrThrow()?.forEach {
                    daftarKavling.add(it)
                }
            }
            val onlyKavlingAdaUser = daftarKavling.filter { !it.belumIsi }

            val mapPembayaran = mutableMapOf<String, List<Pembayaran>?>()
            onlyKavlingAdaUser.forEach { kavling ->
                mapPembayaran[kavling.kode] = pembayaranRepository.getAllPembayaran(kavling.kode, DataMode.ONLINE).first().getOrThrow()
            }

            val sudahBayar = mutableListOf<String>()
            val belumBayar = mutableListOf<String>()
            mapPembayaran.keys.forEach { kavling ->
                val listPembayaran = mapPembayaran[kavling]
                val listPadaBulanAngsuran = listPembayaran?.filter {
                    it.bulanAngsuran == request.bulanAngsuran
                }

                if (listPadaBulanAngsuran.isNullOrEmpty()) {
                    belumBayar.add(kavling)
                } else {
                    sudahBayar.add(kavling)
                }
            }

            val result = buildList {
                add(0, sudahBayar)
                add(1, belumBayar)
            }
            emit(Result.success(result))
        }
    }


}