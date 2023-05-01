package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pengingat

interface PengingatRepository {

    fun getAll(): Flow<Result<List<Pengingat>?>>

    fun getSingleById(id: Long): Flow<Result<Pengingat?>>

    fun insert(pengingat: Pengingat): Flow<Result<Long?>>

    fun update(oldPengingat: Pengingat, newPengingat: Pengingat): Flow<Result<Nothing?>>

    fun delete(pengingat: Pengingat): Flow<Result<Nothing?>>
}