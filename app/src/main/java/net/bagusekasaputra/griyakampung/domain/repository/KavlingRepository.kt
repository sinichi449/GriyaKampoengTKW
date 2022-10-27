package net.bagusekasaputra.griyakampung.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampung.domain.entity.Kavling

interface KavlingRepository {

    fun getKavlingByKode(kode: String): Flow<List<Kavling>>

    fun addKavling(kode: String): Flow<Boolean>
}