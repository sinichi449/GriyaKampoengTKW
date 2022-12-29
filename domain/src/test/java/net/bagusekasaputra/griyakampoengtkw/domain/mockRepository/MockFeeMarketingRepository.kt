package net.bagusekasaputra.griyakampoengtkw.domain.mockRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FeeMarketingRepository

class MockFeeMarketingRepository: FeeMarketingRepository {

    private val mapFeeMarketing = mutableMapOf<String, FeeMarketing?>(
        Pair("A2", FeeMarketing("A2", "Sridevi", "9,800,000", tanggalPenerimaan = "01/03/2021"))
    )

    override fun getBatch(listKavling: List<String>): Flow<Result<Map<String, FeeMarketing?>?>> {
        return flow {
            val result = mutableMapOf<String, FeeMarketing?>()

            listKavling.forEach { kavling ->
                result[kavling] = mapFeeMarketing[kavling]
            }

            emit(Result.success(result))
        }
    }

    override fun getByKavlingKode(
        kavlingKode: String,
        offline: Boolean,
    ): Flow<Result<FeeMarketing?>> {
        return flow {
            emit(Result.success(mapFeeMarketing[kavlingKode]))
        }
    }

    override fun getAllOnline(kavlingKode: String): Flow<Result<FeeMarketing?>> {
        TODO("Not yet implemented")
    }

    override fun addFeeMarketing(feeMarketing: FeeMarketing): Flow<Result<Nothing?>> {
        TODO("Not yet implemented")
    }

    override fun updateFeeMarketing(
        oldFeeMarketing: FeeMarketing,
        newFeeMarketing: FeeMarketing,
    ): Flow<Result<Nothing?>> {
        TODO("Not yet implemented")
    }

    override fun deleteFeeMarketing(kavlingKode: String): Flow<Result<Nothing?>> {
        TODO("Not yet implemented")
    }
}