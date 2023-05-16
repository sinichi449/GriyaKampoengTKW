package net.bagusekasaputra.griyakampoengtkw

import android.util.Log
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BaselinePembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CacheAccumulatorForProgressKavling @Inject constructor(
    private val blockRepository: BlockRepository,
    private val kavlingRepository: KavlingRepository,
    private val baselinePembayaranRepository: BaselinePembayaranRepository,
    private val dataDiriRepository: DataDiriRepository,
    private val pembayaranRepository: PembayaranRepository,
) {

    private val dataMode = DataMode.ONLINE

    suspend fun execute(): Result<Nothing?> {
        return try {
            val blocks = hoardAllBlocks()
            val kavlings = hoardKavlings(blocks)

            hoardDataDiris(kavlings)
            hoardBaselinePembayarans(kavlings)
            hoardPembayarans(kavlings)

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    private suspend fun hoardAllBlocks(): List<Block> {
        blockRepository.refreshCache()

        val result = blockRepository.getAllBlocks(DataMode.OFFLINE).first()
        val blocks = result.getOrThrow()

        blocks?.forEach { block ->
            logMessage("getAllBlock() -> Success retrieving Blok ${block.kode}!")
        }

        return if (blocks.isNullOrEmpty()) {
            emptyList()
        } else {
            blocks
        }
    }

    private suspend fun hoardKavlings(listBlock: List<Block>): List<Kavling> {
        kavlingRepository.refreshCache(listBlock).getOrThrow()

        val kavlings = mutableListOf<Kavling>()
        listBlock.forEach { blok ->
            val result = kavlingRepository.getKavlingByBlock(blok.kode, DataMode.OFFLINE).first()
            val listKavling = result.getOrThrow()


            listKavling?.also {
                it.forEach { kavling -> logMessage("getKavlings() -> Kavling ${kavling.kode} retrieved!")}

                kavlings.addAll(it)
            }
        }

        return kavlings
    }

    private suspend fun hoardDataDiris(kavlings: List<Kavling>) {
        kavlings.forEach { kavling ->
            val kodeKavling = kavling.kode
            val dataDiri = dataDiriRepository.getDataDiri(kodeKavling, dataMode).first()
                .getOrThrow()

            dataDiri?.also { logMessage("hoardDataDiris() -> Data Diri Kav. $kodeKavling acquired!") }
        }
    }

    private suspend fun hoardBaselinePembayarans(listKavling: List<Kavling>) {
        listKavling.forEach { kavling ->
            val kodeKavling = kavling.kode

            val baselinePembayaran = baselinePembayaranRepository.get(kodeKavling, dataMode)
                .first()
                .getOrThrow()

            baselinePembayaran?.also { logMessage("hoardBaselinePembayaran() -> Baseline Pembayaran ${it.kavling} queried!") }
        }
    }

    private suspend fun hoardPembayarans(kavlings: List<Kavling>) {
        logMessage("hoardPembayarans() -> On Progress hoarding Pembayarans ...")

        val kavlingStrs = mutableListOf<String>().run {
            kavlings.forEach { kavling ->
                add(kavling.kode)
            }

            this
        }
        val refreshCacheResult = pembayaranRepository.refreshCache(kavlingStrs)

        if (refreshCacheResult.isSuccess) {
            logMessage("hoardPembayarans() -> SUCCESS Hoarding Pembayarans!")
        } else {
            refreshCacheResult.exceptionOrNull()?.also { throw it }
        }
    }

    private fun logMessage(msg: String) {
        Log.d("INIT_CACHE", msg)
    }
}