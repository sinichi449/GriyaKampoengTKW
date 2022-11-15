package net.bagusekasaputra.griyakampoengtkw.domain.usecase.biayaMarketing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMarketingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllBiayaMarketingByKavlingKodeUseCase @Inject constructor(
    private val biayaMarketingRepository: BiayaMarketingRepository,
): UseCase<GetAllBiayaMarketingByKavlingKodeUseCase.Request, GetAllBiayaMarketingByKavlingKodeUseCase.Response>() {

    data class Request(val kavlingKode: String): UseCase.Request

    data class Response(val result: Result<List<BiayaMarketing>?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return biayaMarketingRepository.getAllByKavlingKode(request.kavlingKode).map { result ->
            Response(result.map { listBiayaMarketing ->
                listBiayaMarketing?.let {
                    hitungTotalBiaya(it)
                }
            })
        }
    }

    private fun hitungTotalBiaya(listBiayaMarketing: List<BiayaMarketing>): List<BiayaMarketing> {
        val newBiayaMarketingList = ArrayList<BiayaMarketing>()
        var totalBiaya = 0L

        listBiayaMarketing.forEach { biayaMarketing ->
            totalBiaya += biayaMarketing.harga.toLong()

            biayaMarketing.totalBiaya = totalBiaya.toString()

            newBiayaMarketingList.add(biayaMarketing)
        }

        return newBiayaMarketingList
    }
}