package net.bagusekasaputra.griyakampoengtkw.domain.mockRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FeeMarketingRepository

class MockFeeMarketingRepository: FeeMarketingRepository {

    override fun getByKavlingKode(
        kavlingKode: String,
        offline: Boolean,
    ): Flow<Result<FeeMarketing?>> {
        return flow {
            val feeMarketing = when (kavlingKode) {
                "A2" -> FeeMarketing(
                    kavlingKode = "A2",
                    namaMarketer = "Gunawan",
                    biayaMarketer = "4,000,000",
                    tanggalPenerimaan = "13/09/2022",
                )
                "A3" -> FeeMarketing(
                    kavlingKode = "A3",
                    namaMarketer = "Mbak Novi",
                    biayaMarketer = "3,550,000",
                    tanggalPenerimaan = "01/01/2021",
                )
                "A4" -> FeeMarketing(
                    kavlingKode = "A4",
                    namaMarketer = "Watinah",
                    biayaMarketer = "5,800,000",
                    tanggalPenerimaan = "26/05/2022",
                )
                else -> null
            }

            emit(Result.success(feeMarketing))
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