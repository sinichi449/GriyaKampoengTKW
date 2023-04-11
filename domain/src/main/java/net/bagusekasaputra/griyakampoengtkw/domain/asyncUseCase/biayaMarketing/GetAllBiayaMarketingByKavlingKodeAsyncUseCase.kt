package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaMarketing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.zip
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMarketingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FeeMarketingRepository

class GetAllBiayaMarketingByKavlingKodeAsyncUseCase(
    private val biayaMarketingRepository: BiayaMarketingRepository,
    private val feeMarketingRepository: FeeMarketingRepository,
): AsyncUseCase<GetAllBiayaMarketingByKavlingKodeAsyncUseCase.Request, List<BiayaMarketing>?>() {

    data class Request(val kavlingKode: String, val dataMode: DataMode): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<BiayaMarketing>?>> {
        return biayaMarketingRepository.getAllByKavlingKode(request.kavlingKode, request.dataMode)
            .zip(getBiayaMarketer(request.kavlingKode, false)) { resultBiayaMarketing, biayaMarketer ->
                val listBiayaMarketing = resultBiayaMarketing.getOrNull()

                if (listBiayaMarketing != null) {
                    val maskedBiayaMarketing = maskBiayaMarketing(listBiayaMarketing, biayaMarketer)

                    return@zip Result.success(maskedBiayaMarketing)
                } else {
                    return@zip resultBiayaMarketing
                }
            }
    }

    private fun maskBiayaMarketing(
        listBiayaMarketing: List<BiayaMarketing>,
        biayaMarketer: Long?,
    ): List<BiayaMarketing> {
        val newBiayaMarketingList = ArrayList<BiayaMarketing>()

        // Calculate hargaDanTambahLuasan biaya, together with biayaMarketer
        var totalBiaya = biayaMarketer ?: 0L

        listBiayaMarketing.forEach { biayaMarketing ->
            totalBiaya += biayaMarketing.harga.toLong()

            biayaMarketing.totalBiaya = totalBiaya.toString()

            newBiayaMarketingList.add(biayaMarketing)
        }

        return newBiayaMarketingList
    }

    private fun getBiayaMarketer(kavlingKode: String, offline: Boolean): Flow<Long?> {
        return feeMarketingRepository.getByKavlingKode(kavlingKode, offline).map { result ->
            result.getOrNull()?.biayaMarketer?.toLong()
        }
    }
}