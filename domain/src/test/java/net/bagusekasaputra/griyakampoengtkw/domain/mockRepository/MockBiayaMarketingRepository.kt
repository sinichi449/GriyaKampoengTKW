package net.bagusekasaputra.griyakampoengtkw.domain.mockRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMarketingRepository

class MockBiayaMarketingRepository: BiayaMarketingRepository {

    override fun getAllByKavlingKode(
        kavlingKode: String,
        offline: Boolean,
    ): Flow<Result<List<BiayaMarketing>?>> {
        return flow {
            val minimumBiayaMarketing = 1L
            val maximumBiayaMarketing = 100L

            val listBiayaMarketing = when (kavlingKode) {
                "A2" -> listOf(
                    BiayaMarketing(kavlingKode = "A2", tanggal = "13/09/2022", jenisBiaya = "biaya marketing 1", harga = NumberUtil.formatLongToString(MockUtils.getRandomDuwitValue(minimumBiayaMarketing, maximumBiayaMarketing))),
                    BiayaMarketing(kavlingKode = "A2", tanggal = "13/09/2022", jenisBiaya = "biaya marketing 2", harga = NumberUtil.formatLongToString(MockUtils.getRandomDuwitValue(minimumBiayaMarketing, maximumBiayaMarketing))),
                    BiayaMarketing(kavlingKode = "A2", tanggal = "13/09/2022", jenisBiaya = "biaya marketing 3", harga = NumberUtil.formatLongToString(MockUtils.getRandomDuwitValue(minimumBiayaMarketing, maximumBiayaMarketing))),
                )
                "A3" -> listOf(
                    BiayaMarketing(kavlingKode = "A3", tanggal = "22/06/2022", jenisBiaya = "biaya marketing 1", harga = NumberUtil.formatLongToString(MockUtils.getRandomDuwitValue(minimumBiayaMarketing, maximumBiayaMarketing))),
                    BiayaMarketing(kavlingKode = "A3", tanggal = "22/06/2022", jenisBiaya = "biaya marketing 2", harga = NumberUtil.formatLongToString(MockUtils.getRandomDuwitValue(minimumBiayaMarketing, maximumBiayaMarketing))),
                    BiayaMarketing(kavlingKode = "A3", tanggal = "22/06/2022", jenisBiaya = "biaya marketing 3", harga = NumberUtil.formatLongToString(MockUtils.getRandomDuwitValue(minimumBiayaMarketing, maximumBiayaMarketing))),
                    BiayaMarketing(kavlingKode = "A3", tanggal = "22/06/2022", jenisBiaya = "biaya marketing 4", harga = NumberUtil.formatLongToString(MockUtils.getRandomDuwitValue(minimumBiayaMarketing, maximumBiayaMarketing))),
                )
                "A4" -> listOf(
                    BiayaMarketing(kavlingKode = "A4", tanggal = "09/09/2022", jenisBiaya = "biaya marketing 1", harga = NumberUtil.formatLongToString(MockUtils.getRandomDuwitValue(minimumBiayaMarketing, maximumBiayaMarketing))),
                    BiayaMarketing(kavlingKode = "A4", tanggal = "09/09/2022", jenisBiaya = "biaya marketing 2", harga = NumberUtil.formatLongToString(MockUtils.getRandomDuwitValue(minimumBiayaMarketing, maximumBiayaMarketing))),
                )
                else -> null
            }

            emit(Result.success(listBiayaMarketing))
        }
    }

    override fun getAllOnline(kavlingKode: String): Flow<Result<List<BiayaMarketing>?>> {
        TODO("Not yet implemented")
    }

    override fun addBiayaMarketing(biayaMarketing: BiayaMarketing): Flow<Result<Nothing?>> {
        TODO("Not yet implemented")
    }

    override fun update(
        oldBiayaMarketing: BiayaMarketing,
        newBiayaMarketing: BiayaMarketing,
    ): Flow<Result<Nothing?>> {
        TODO("Not yet implemented")
    }

    override fun deleteSingle(
        kavlingKode: String,
        biayaMarketing: BiayaMarketing,
    ): Flow<Result<Nothing?>> {
        TODO("Not yet implemented")
    }

    override fun deleteAll(kavlingKode: String): Flow<Result<Nothing?>> {
        TODO("Not yet implemented")
    }
}