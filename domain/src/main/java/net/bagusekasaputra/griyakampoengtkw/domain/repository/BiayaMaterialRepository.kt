package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.BiayaMaterial

interface BiayaMaterialRepository {

    fun getAsFlow(dataMode: DataMode): Flow<Result<BiayaMaterial?>>

    suspend fun getByKeyId(keyId: String, dataMode: DataMode): Result<BiayaMaterial?>

    suspend fun insert(biayaMaterial: BiayaMaterial): Result<Unit>

    suspend fun update(keyId: String, newBiayaMaterial: BiayaMaterial): Result<Unit>

    suspend fun delete(keyId: String): Result<Unit>

}