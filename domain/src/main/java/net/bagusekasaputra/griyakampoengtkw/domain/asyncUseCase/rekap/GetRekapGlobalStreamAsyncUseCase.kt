package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran.Companion.sortByTermin
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran.Companion.tanggalPembelian
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran.Companion.totalUangMasuk
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.RekapGlobal
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository

/**
 * Get a stream of [RekapGlobal] data.
 */
class GetRekapGlobalStreamAsyncUseCase(
    private val dataDiriRepository: DataDiriRepository,
    private val pembayaranRepository: PembayaranRepository,
    private val hargaKavlingRepository: HargaKavlingRepository,
): AsyncUseCase<GetRekapGlobalStreamAsyncUseCase.Request, List<RekapGlobal>>() {

    /**
     * @param [kavlingList] specify [Kavling.kode] to a [List] of [String] which you want to get the [RekapGlobal] of.
     * If you leave this arguments as an [emptyList], then this use case will assume all available [Kavling]
     * in [KavlingRepository].
     * **Note**: if you pass an [emptyList], currently will throw a [NotImplementedError].
     *
     * @param [excludedList] specify [Kavling.kode] to a [List] of [String] which you want to _exclude_
     * from fetching [RekapGlobal]. If you leave this arguments as an [emptyList], then this use case will assume all
     * available _excluded_ [Kavling] in [KavlingRepository], which are pre-configured to be _excluded_.
     * **Note**: if you pass an [emptyList], currently will throw a [NotImplementedError].
     *
     * @param dataMode prefer [DataMode.ONLINE] as the other [DataMode] are either will throw a [NotImplementedError]
     * or simply buggy.
     *
     */
    data class Request(
        val kavlingList: List<String> = emptyList(),
        val excludedList: List<String> = emptyList(),
        val dataMode: DataMode = DataMode.ONLINE,
    ): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<RekapGlobal>?>> {
        return flow {
            val resultList = mutableListOf<RekapGlobal>()

            val kavlingList = if (request.kavlingList.isNotEmpty()) {
                request.kavlingList
            } else {
                throw NotImplementedError("Masih dalam pengembangan")
            }

            kavlingList.forEach { kavling ->
                if (!request.excludedList.contains(kavling)) {
                    val dataMode = request.dataMode
                    val dataDiri = dataDiriRepository
                        .getDataDiri(kavling, dataMode)
                        .firstAndEmitError(this)
                    val pembayaranList = pembayaranRepository
                        .getAllPembayaran(kavling, dataMode)
                        .firstAndEmitError(this)
                    val hargaKavling = hargaKavlingRepository
                        .getHargaKavling(kavling, dataMode)
                        .firstAndEmitError(this)

                    val nama = dataDiri?.nama ?: "-"
                    val hargaDanTambahLuasan = hargaKavling?.hargaDanTambahLuasan ?: 0L
                    /* [pembayaranList] should be sorted! */
                    val sortedPembayaran = pembayaranList?.sortByTermin()
                    val tglPembelian = if (sortedPembayaran.isNullOrEmpty())
                        null else sortedPembayaran.tanggalPembelian().toDate()
                    val uangMasuk = sortedPembayaran?.totalUangMasuk() ?: 0L


                    resultList.add(RekapGlobal(
                        noKavling = kavling,
                        namaCostumer = nama,
                        tanggalPembelian = tglPembelian,
                        harga = hargaDanTambahLuasan,
                        jumlahUangMasuk = uangMasuk,
                    ))
                    if (resultList.isNotEmpty()) {
                        emit(Result.success(resultList))
                    } else {
                        emit(Result.success(null))
                    }
                }
            }
        }
    }

    private suspend fun <T, S> Flow<Result<T>>.firstAndEmitError(
        collector: FlowCollector<Result<S>>
    ): T? {
        return first()
            .onFailure { collector.emit(Result.failure(it)) }
            .getOrNull()
    }
}