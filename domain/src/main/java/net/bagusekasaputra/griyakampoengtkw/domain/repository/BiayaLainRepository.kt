package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain

interface BiayaLainRepository {

    fun getAll(offline: Boolean): Flow<Result<List<BiayaLain>?>>

    fun getSingle(jenisBiaya: String, offline: Boolean): Flow<Result<BiayaLain?>>

    fun addBiayaLain(biayaLain: BiayaLain): Flow<Result<Nothing?>>

    fun updateBiayaLain(oldBiayaLain: BiayaLain, newBiayaLain: BiayaLain): Flow<Result<Nothing?>>

    fun deleteBiayaLain(biayaLain: BiayaLain): Flow<Result<Nothing?>>

}