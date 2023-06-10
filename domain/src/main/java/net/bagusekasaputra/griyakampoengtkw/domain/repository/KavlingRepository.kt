package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.Kavling

interface KavlingRepository {

    fun getAsFlow(blok: String): Flow<Result<Kavling?>>

    fun getKavlingByBlock(blockCode: String, dataMode: DataMode): Flow<Result<List<Kavling>?>>

    fun getAllKavlings(blockKodes: List<String>): Flow<Result<HashMap<String, List<Kavling>>?>>

    fun addKavling(blockKode: String, kavling: Kavling): Flow<Result<Nothing?>>

    fun updateKavling(blockCode: String, oldKavling: Kavling, newKavling: Kavling): Flow<Result<Nothing?>>

    fun removeKavling(blockCode: String, kavlingKode: String): Flow<Result<Nothing?>>

    fun getUnmigratedKavlings(backupName: String): Flow<Result<List<String>?>>

    suspend fun getRekapExclusionList(): Result<List<String>?>

    // If List<Block> parameter left empty, then it will get List<Block> from cache
    suspend fun refreshCache(blocks: List<Block> = emptyList()): Result<Nothing?>
}