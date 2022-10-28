package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling

interface KavlingRepository {

    fun getKavlingByBlock(block: Block): Flow<List<Kavling>>

    fun addKavling(kode: String): Flow<Boolean>
}