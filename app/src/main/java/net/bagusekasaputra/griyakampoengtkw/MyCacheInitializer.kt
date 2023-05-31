package net.bagusekasaputra.griyakampoengtkw

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BaselinePembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.interfaces.CacheInitializer
import kotlin.coroutines.CoroutineContext

class MyCacheInitializer(
    private val sharedPrefs: SharedPreferences,
    private val coroutineContext: CoroutineContext,
    private val blockRepository: BlockRepository,
    private val kavlingRepository: KavlingRepository,
    private val dataDiriRepository: DataDiriRepository,
    private val baselinePembayaranRepository: BaselinePembayaranRepository,
    private val pembayaranRepository: PembayaranRepository,

): CacheInitializer {

    private val TAG = "INIT_CACHE"

    override suspend fun initialize(): Result<Nothing?> {
        return if (isUnitialized()) {
            callbackFlow<Result<Nothing?>> {
                try {
                    // Get blocks and kavlings
                    val blocks = withContext(Dispatchers.IO) {
                        val listBlocks = blockRepository.getAllBlocks(DataMode.ONLINE).first()
                            .getOrThrow()

                        // Logging
                        listBlocks?.forEach {
                            Log.d(TAG, "Got block ${it.kode} !")
                        }

                        listBlocks
                    }
                    val kavlings = withContext(Dispatchers.IO) {
                        if (blocks.isNullOrEmpty()) {
                            Log.d(TAG, "Uninitialized Kavlings because Blocks are either NULL or EMPTY")

                            null
                        } else {
                            val blockKodes = blocks.map { it.kode }
                            val mapBlockAndKavling = kavlingRepository.getAllKavlings(blockKodes).first()
                                .getOrThrow()

                            val kavlings = mutableListOf<String>()
                            mapBlockAndKavling?.entries?.forEach {
                                val kodeOnlys = it.value.map { kavling -> kavling.kode }

                                kavlings.addAll(kodeOnlys)
                            }

                            // Logging
                            kavlings.forEach {
                                Log.d(TAG, "Kavling $it acquired!")
                            }

                            if (kavlings.isEmpty()) null
                            else kavlings.toList()
                        }
                    }

                    // Only initialize the rest if Kavlings is not null or empty
                    if (kavlings.isNullOrEmpty()) {
                        Log.d(TAG, "Unitialized Baselines, Harga Kavlings, Pembayarans, Data Diri, etc because Kavlings are either NULL or empty!")

                        setCacheHasBeenInitialized(false)
                    } else {
                        Log.d(TAG, "Initializing Data Diri ...")
                        dataDiriRepository.refreshCache(kavlings).getOrThrow()

                        Log.d(TAG, "Initializing Baseline Pembayaran ...")
                        baselinePembayaranRepository.refreshCache(kavlings).getOrThrow()

                        Log.d(TAG, "Initializing Pembayaran ...")
                        pembayaranRepository.refreshCache(kavlings).getOrThrow()

                        setCacheHasBeenInitialized(true)
                    }

                    trySendBlocking(Result.success(null))
                } catch (e: Exception) {
                    setCacheHasBeenInitialized(false)

                    trySendBlocking(Result.failure(e))
                }

                awaitClose {  }
            }
                .flowOn(coroutineContext)
                .first()
        } else {
            Result.success(null)
        }
    }

    override fun isUnitialized(): Boolean {
        return sharedPrefs.getBoolean(
            ConstsSharedPrefs.CACHE_UNINITIALIZED_OR_DESTROYED,
            true
        )
    }

    private fun setCacheHasBeenInitialized(initialized: Boolean) {
        sharedPrefs.edit(true) {
            putBoolean(ConstsSharedPrefs.CACHE_UNINITIALIZED_OR_DESTROYED, !initialized)
        }
    }
}