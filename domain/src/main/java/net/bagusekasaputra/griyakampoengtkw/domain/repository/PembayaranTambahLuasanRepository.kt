package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.PembayaranTambahLuasan

interface PembayaranTambahLuasanRepository {

    fun getAll(kavling: String): Flow<Result<List<PembayaranTambahLuasan>?>>

}