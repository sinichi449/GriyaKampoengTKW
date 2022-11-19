package net.bagusekasaputra.griyakampoengtkw.domain.usecase.biayaMarketing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.zip
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMarketingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FeeMarketingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase

class GetAllBiayaMarketingByKavlingKodeUseCase(
    private val biayaMarketingRepository: BiayaMarketingRepository,
    private val feeMarketingRepository: FeeMarketingRepository,
): UseCase<GetAllBiayaMarketingByKavlingKodeUseCase.Request, GetAllBiayaMarketingByKavlingKodeUseCase.Response>() {

    data class Request(val kavlingKode: String): UseCase.Request

    data class Response(val result: Result<List<BiayaMarketing>?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return biayaMarketingRepository.getAllByKavlingKode(request.kavlingKode)
            .zip(getBiayaMarketer(request.kavlingKode)) { resultBiayaMarketing, biayaMarketer ->
                val listBiayaMarketing = resultBiayaMarketing.getOrNull()

                if (listBiayaMarketing != null) {
                    val maskedBiayaMarketing = maskBiayaMarketing(listBiayaMarketing, biayaMarketer)

                    return@zip Result.success(maskedBiayaMarketing)
                } else {
                    return@zip resultBiayaMarketing
                }
            }
            .map {
                Response(it)
            }
    }

    private fun maskBiayaMarketing(
        listBiayaMarketing: List<BiayaMarketing>,
        biayaMarketer: Long?,
    ): List<BiayaMarketing> {
        val newBiayaMarketingList = ArrayList<BiayaMarketing>()
        // We add biaya marketer firstly
        var totalBiaya = biayaMarketer ?: 0L

        listBiayaMarketing.forEach { biayaMarketing ->
            totalBiaya += biayaMarketing.harga.toLong()

            biayaMarketing.totalBiaya = totalBiaya.toString()

            newBiayaMarketingList.add(biayaMarketing)
        }

        return newBiayaMarketingList
    }

    private fun getBiayaMarketer(kavlingKode: String): Flow<Long?> {
        return feeMarketingRepository.getByKavlingKode(kavlingKode).map { result ->
            result.getOrNull()?.biayaMarketer?.toLong()
        }
    }
}