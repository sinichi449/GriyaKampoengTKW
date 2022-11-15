package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing

interface BiayaMarketingRepository {

    fun getAllByKavlingKode(kavlingKode: String): Flow<Result<List<BiayaMarketing>?>>

    fun addBiayaMarketing(biayaMarketing: BiayaMarketing): Flow<Result<Nothing?>>

    fun update(oldBiayaMarketing: BiayaMarketing, newBiayaMarketing: BiayaMarketing): Flow<Result<Nothing?>>

    fun deleteByKavlingKode(kavlingKode: String): Flow<Result<Nothing?>>
}