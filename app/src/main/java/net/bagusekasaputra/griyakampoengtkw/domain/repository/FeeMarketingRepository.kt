package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing

interface FeeMarketingRepository {

    fun getByKavlingKode(kavlingKode: String): Flow<Result<FeeMarketing?>>

    fun addFeeMarketing(feeMarketing: FeeMarketing): Flow<Result<Nothing?>>

    fun updateFeeMarketing(oldFeeMarketing: FeeMarketing, newFeeMarketing: FeeMarketing): Flow<Result<Nothing?>>

    fun deleteFeeMarketing(kavlingKode: String): Flow<Result<Nothing?>>
}