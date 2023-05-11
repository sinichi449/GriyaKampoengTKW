package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository

class GetAllPembayaranAsyncUseCase(
    private val pembayaranRepository: PembayaranRepository,
    private val hargaKavlingRepository: HargaKavlingRepository,

    // Below Foto Pembayaran repository is used to mark whether a specific Pembayaran
    // already filled with Foto Pembayaran. To know that, I will modify the
    // "sudahIsiFotoPembayaran" property in Pembayaran entity.
    private val fotoPembayaranRepository: FotoPembayaranRepository,
): AsyncUseCase<GetAllPembayaranAsyncUseCase.Request, List<Pembayaran>?>() {

    data class Request(
        val kavlingKode: String,
        val dataMode: DataMode
    ): AsyncUseCase.Request


    override fun process(request: Request): Flow<Result<List<Pembayaran>?>> {
        return flow {
            // TODO
        }
    }

}