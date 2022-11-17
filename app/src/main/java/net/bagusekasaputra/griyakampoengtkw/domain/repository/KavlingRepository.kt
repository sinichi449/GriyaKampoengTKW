package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling

interface KavlingRepository {

    fun getKavlingByBlock(blockCode: String): Flow<Result<List<Kavling>?>>

    fun addKavling(blockKode: String, kavling: Kavling): Flow<Result<Nothing?>>

    fun updateKavling(blockCode: String, oldKavling: Kavling, newKavling: Kavling): Flow<Result<Nothing?>>

    fun removeKavling(blockCode: String, kavlingKode: String): Flow<Result<Nothing?>>
}