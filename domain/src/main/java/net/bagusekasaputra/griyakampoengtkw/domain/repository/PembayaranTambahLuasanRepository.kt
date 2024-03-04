package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.PembayaranTambahLuasan

interface PembayaranTambahLuasanRepository {

    fun getAll(kavling: String): Flow<Result<List<PembayaranTambahLuasan>?>>

    fun get(kavling: String, id: String): Flow<Result<PembayaranTambahLuasan?>>

    fun add(pembayaranTambahLuasan: PembayaranTambahLuasan): Flow<Result<Nothing?>>

    fun delete(kavling: String, id: String): Flow<Result<Nothing?>>

    fun update(oldId: String, newEntity: PembayaranTambahLuasan): Flow<Result<Nothing?>>

}