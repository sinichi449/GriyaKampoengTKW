package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.CacheHelper
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper.mapPembayaran
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemotePembayaranSource
import net.bagusekasaputra.griyakampoengtkw.data.model.MetadataModel
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import java.util.Calendar

class PembayaranRepositoryImpl(
    private val localPembayaranDataSource: LocalPembayaranDataSource,
    private val remotePembayaranSource: RemotePembayaranSource,
    private val backupPembayaranDataSource: BackupPembayaranDataSource,
    private val localMetadata: LocalMetadataDataSource,
    private val remoteMetadata: RemoteMetadataDataSource,
    private val cacheHelper: CacheHelper,
): PembayaranRepository {

    private val metadataTable = "formPembayaran"
    private val pembayaranIndenBookingLocalTable = "pembayaranIndenBooking"
    private val pembayaranIndenBookingRemoteTable = "indenBooking/pembayaran"

    override fun getBatchOnline(listKavling: List<String>): Flow<Result<Map<String, List<Pembayaran>?>?>> {
        return flow {
            checkCache()

            val batchPembayaran = mutableMapOf<String, List<Pembayaran>?>()

            // Consolidating models
            listKavling.forEach { kavling ->
                val localModel = localPembayaranDataSource.getAllPembayaran(kavling)
                    .onFailure {
                        Log.d("DEBUG_ME", "FAILED attempt to GET Pembayaran $kavling Local : ${it.message}")
                    }
                    .getOrNull()

                if (localModel.isNullOrEmpty()) {
                    remotePembayaranSource.getAllPembayaran(kavling)
                        .onSuccess { listPembayaran ->
                            listPembayaran?.forEach {
                                localPembayaranDataSource.addPembayaranModel(
                                    kavlingKode = kavling,
                                    hargaKavling = 0L, // TODO: What is this for??
                                    pembayaranModel = it
                                ).onFailure {
                                    Log.d("DEBUG_ME", "PembayaranRepoImpl:53 onFailure -> ${it.message}")
                                }
                            }
                        }
                        .onFailure {
                            Log.d("DEBUG_ME", "FAILED attempt to GET Pembayaran $kavling Remote : ${it.message}")
                        }

                    // Second try
                    localPembayaranDataSource.getAllPembayaran(kavling)
                        .onSuccess { listPembayaran ->
                            batchPembayaran[kavling] = listPembayaran?.map { mapPembayaran(it) }
                        }
                        .onFailure {
                            Log.d("DEBUG_ME", "FAILED attempt to GET Pembayaran $kavling on PembaranRepoImpl:63 : ${it.message}")
                        }
                } else {
                    batchPembayaran[kavling] = localModel.map { mapPembayaran(it) }
                }
            }

            emit(Result.success(batchPembayaran))
        }
    }

    private fun getFromDataLama(kavlingKode: String): Flow<Result<List<Pembayaran>?>> {
        return flow {
            val backupResult = backupPembayaranDataSource.getAllPembayaran(kavlingKode)
            val mapResult = DataUtil.mapListResult(
                originResult = backupResult,
                targetMapper = ::mapPembayaran,
            )

            emit(mapResult)
        }
    }

    override fun getBatchBackup(listKavling: List<String>): Flow<Result<Map<String, List<Pembayaran>?>?>> {
        return callbackFlow {
            try {
                val mapListPembayaran = mutableMapOf<String, List<Pembayaran>?>()

                listKavling.forEach { kavling ->
                    mapListPembayaran[kavling] = getFromDataLama(kavling).first().getOrThrow()
                }

                trySendBlocking(Result.success(mapListPembayaran))
            } catch (e: Exception) {
                e.printStackTrace()

                trySendBlocking(Result.failure(e))
            }

            awaitClose {  }
        }
    }

    override fun getBatchFromRemoteBackup(
        backupName: String,
        listKavling: List<String>
    ): Flow<Result<Map<String, List<Pembayaran>?>?>> {
        return flow {
            val mapPembayaran = mutableMapOf<String, List<Pembayaran>?>()

            listKavling.forEach { kavling ->
                val remoteResult = remotePembayaranSource.getAllFromBackup(backupName, kavling)
                if (remoteResult.isSuccess) {
                    val listModel = remoteResult.getOrNull()
                    val listPembayaran = listModel?.map { mapPembayaran(it) }

                    mapPembayaran[kavling] = listPembayaran
                } else {
                    val errorCause = remoteResult.exceptionOrNull() ?: Throwable("Unknown Error getting Backup \"$backupName\" List Pembayaran at kavling $kavling")

                    errorCause.printStackTrace()

                    emit(Result.failure(errorCause))
                }
            }

            emit(Result.success(mapPembayaran))
        }
    }

    override fun getAllPembayaran(
        kavlingKode: String,
        dataMode: DataMode,
    ): Flow<Result<List<Pembayaran>?>> {
        return flow {
            // TODO: Check metadata

            val flowOffline = flow {
                val localResult = localPembayaranDataSource.getAllPembayaran(kavlingKode)
                val mapResult = DataUtil.mapListResult(
                    originResult = localResult,
                    targetMapper = ::mapPembayaran,
                )

                emit(mapResult)
            }
            val flowOnline = flow {
                // First, we request to the remote
                val remoteResult = remotePembayaranSource.getAllPembayaran(kavlingKode)

                if (remoteResult.isSuccess) {
                    // If success, then we write to the local data
                    remoteResult.getOrNull()?.forEach {
                        localPembayaranDataSource.addPembayaranModel(kavlingKode, 0L, it)
                    }

                    // Then emit the result
                    val mapResult = DataUtil.mapListResult(
                        originResult = remoteResult,
                        targetMapper = ::mapPembayaran,
                    )
                    emit(mapResult)
                } else {
                    // If remote request is failed, we emit the error
                    val errorCause = remoteResult.exceptionOrNull()
                        ?: Throwable("Terjadi kesalahan tak diketahui pada server saat mendapatkan data Pembayaran!")
                    emit(Result.failure(errorCause))

                    // Then get from local instead
                    emitAll(flowOffline)
                }
            }
            val flowDataLama = getFromDataLama(kavlingKode)


            when (dataMode) {
                DataMode.ONLINE -> emitAll(flowOnline)
                DataMode.OFFLINE -> emitAll(flowOffline)
                DataMode.DATA_LAMA -> emitAll(flowDataLama)
            }
        }
    }

    override fun getAllOnline(kavlingKode: String): Flow<Result<List<Pembayaran>?>> {
        return flow {
            // First, we request to the remote
            val remoteResult = remotePembayaranSource.getAllPembayaran(kavlingKode)

            val mapResult = DataUtil.mapListResult(
                originResult = remoteResult,
                targetMapper = ::mapPembayaran,
            )
            emit(mapResult)
        }
    }

    override suspend fun sudahBayarAngsuran(
        kavlingKode: String,
        bulan: Int,
        dataMode: DataMode
    ): Result<Boolean?> {
        return try {
            val cacheListPembayaran = if (dataMode == DataMode.DATA_LAMA)
                backupPembayaranDataSource.getAllPembayaran(kavlingKode)
                    .getOrNull()
            else
                localPembayaranDataSource.getAllPembayaran(kavlingKode)
                    .getOrNull()

            if (cacheListPembayaran.isNullOrEmpty()) {
                Result.success(false)
            } else {
                var sudahBayar = false

                for (p in cacheListPembayaran) {
                    val bulanBayar = Calendar.getInstance().let {
                        it.time = p.tanggal.toDate()

                        it.get(Calendar.MONTH) + 1
                    }
                    if (bulan == bulanBayar) {
                        sudahBayar = true
                        break
                    }
                }

                Result.success(sudahBayar)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("DEBUG_ME", "ERROR PembayaranRepo->sudahBayarAngsuran():144 : ${e.message}")

            Result.failure(e)
        }
    }

    override suspend fun getUangMasukBulanIni(kavlingKode: String, dataMode: DataMode): Long? {
        return flow<Long?> {
            val calendar = Calendar.getInstance()
            val bulanSekarang = calendar.get(Calendar.MONTH)
            val tahunSekarang = calendar.get(Calendar.YEAR)

            val pembayaransAll = localPembayaranDataSource.getAllPembayaran(kavlingKode)
                .onFailure { Log.d("STATUS_PEMBAYARAN", "Gagal mendapatkan list pembayaran: ${it.message}") }
                .getOrNull()
            val pembayaransBulanIni = pembayaransAll?.filter {
                val tanggalDibayar = Calendar.getInstance().apply {
                    time = it.tanggal.toDate()
                }
                val bulanBayar = tanggalDibayar.get(Calendar.MONTH)
                val tahunBayar = tanggalDibayar.get(Calendar.YEAR)

                bulanBayar == bulanSekarang && tahunBayar == tahunSekarang
            }

            Log.d("STATUS_PEMBAYARAN", "Pembayaran pada ${bulanSekarang + 1}/${tahunSekarang}: $pembayaransBulanIni")

            if (pembayaransBulanIni != null) {
                val list = pembayaransBulanIni.map { mapPembayaran(it) }

                emit(Pembayaran.hitungTotalUangMasuk(list))
            } else {
                emit(0L)
            }
        }.first()
    }

    override suspend fun refreshCache(kavlings: List<String>): Result<Nothing?> {
        return try {
            localPembayaranDataSource.deleteAll().getOrThrow()
            kavlings.forEach { kavling ->
                val remoteResult = remotePembayaranSource.getAllPembayaran(kavling).getOrThrow()

                remoteResult?.also { pembayaranModels ->
                    localPembayaranDataSource.addAllPembayaranModel(kavling, pembayaranModels)
                }
            }

            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override fun addPembayaran(
        kavlingKode: String,
        hargaKavling: Long,
        pembayaran: Pembayaran,
    ): Flow<Result<Boolean>> {
        return flow {
            updateMetadata()

            val remoteResult = remotePembayaranSource.addPembayaranModel(
                kavlingKode,
                hargaKavling,
                pembayaranModel = mapPembayaran(pembayaran)
            )
            Log.d("DEBUG_ME", "Inserting pembayaran blblbl")

            if (remoteResult.isSuccess) {
                emit(Result.success(true))
            } else {
                val errorCause = remoteResult.exceptionOrNull()
                    ?: Throwable("Terjadi kesalahan tak diketahui saat menambah data Pembayaran!")
                emit(Result.failure(errorCause))
            }
        }
    }

    override fun updatePembayaran(
        kavlingKode: String,
        oldPembayaran: Pembayaran,
        newPembayaran: Pembayaran,
    ): Flow<Result<Boolean>> {
        return flow {
            updateMetadata()

            // We need to update the local too!
            val localResult = localPembayaranDataSource.updatePembayaranModel(
                kavlingKode, mapPembayaran(oldPembayaran), mapPembayaran(newPembayaran)
            )
            localResult.onFailure {
                emit(Result.failure(it))
            }


            val remoteResult = remotePembayaranSource.updatePembayaranModel(
                kavlingKode,
                oldPembayaranModel = mapPembayaran(oldPembayaran),
                newPembayaranModel = mapPembayaran(newPembayaran)
            )
            remoteResult.onSuccess {
                emit(Result.success(true))
            }

            remoteResult.onFailure {
                emit(Result.failure(it))
            }
        }
    }

    override fun deletePembayaranByTermin(
        kavlingKode: String,
        termin: String,
    ): Flow<Result<Boolean>> {
        return flow {
            updateMetadata()

            // delete both from local and remote
            val localResult = localPembayaranDataSource.deletePembayaranModelByTermin(kavlingKode, termin)
            localResult.onFailure {
                emit(Result.failure(it))
            }

            val remoteResult = remotePembayaranSource.deletePembayaranModelByTermin(kavlingKode, termin)
            remoteResult.onSuccess {
                emit(Result.success(true))
            }
            remoteResult.onFailure {
                emit(Result.failure(it))
            }
        }
    }

    /**
     * Inden Booking related
     */
    override suspend fun getAllFromIndenBooking(keyId: String): Result<List<Pembayaran>?> {
        val invalidCache = cacheHelper.checkAndInvalidateCache(
            pembayaranIndenBookingLocalTable,
            pembayaranIndenBookingRemoteTable,
            onInvalid = {
                localPembayaranDataSource.deleteAllFromIndenBooking()
            }
        )
        val localModel = localPembayaranDataSource.getAllFromIndenBooking(keyId).getOrThrow()

        // Fetch from remote data source if either the cache was invalid
        // or the local data source returning null (probably after invalidate() call)
        if (invalidCache || localModel.isNullOrEmpty()) {
            Log.d("INDEN_BOOKING", "Pembayaran on Cache was invalid or Local Data Source is null! ($keyId) " +
                    "Fetching from Remote Data Source now.")

            remotePembayaranSource.getAllFromIndenBooking(keyId).getOrThrow()?.also {
                it.forEach { pembayaranModel ->
                    Log.d("INTERNAL_INDEN_BOOKING", "Begin insertion for ${pembayaranModel.termin} !")
                }
                localPembayaranDataSource.insertAllFromIndenBooking(keyId, it)
            }
        } else {
            Log.d("INDEN_BOOKING", "Pembayaran on Local Data Source is okay, returning from it.")
        }

        val refreshedLocalResult = localPembayaranDataSource.getAllFromIndenBooking(keyId)
        return DataUtil.mapListResult(
            originResult = refreshedLocalResult,
            targetMapper = MyObjectMapper::mapPembayaran,
        )
    }

    override suspend fun insertFromIndenBooking(
        keyId: String,
        pembayaran: Pembayaran
    ): Result<Nothing?> {
        return callbackFlow<Result<Nothing?>> {
            val model = MyObjectMapper.mapPembayaran(pembayaran)

            // Remote Insertion
            remotePembayaranSource.insertFromIndenBooking(keyId, model)
                .onSuccess {
                    // Update Cache
                    cacheHelper.updateMetadata(
                        pembayaranIndenBookingLocalTable, pembayaranIndenBookingRemoteTable
                    )
                        .onSuccess {
                            // Local Insertion
                            localPembayaranDataSource.insertFromIndenBooking(keyId, model)
                                .onSuccess {
                                    trySendBlocking(Result.success(null))
                                }
                                .onFailure {
                                    trySendBlocking(Result.failure(it))
                                }
                        }
                        .onFailure {
                            trySendBlocking(Result.failure(it))
                        }
                }
                .onFailure {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }.first()
    }

    private suspend fun checkCache() {
        // Cache validation
        val localTimestamp = localMetadata.get(metadataTable)?.timestamp
        val remoteTimestamp = remoteMetadata.get(metadataTable)?.timestamp!!
        val cacheInvalid = localTimestamp != remoteTimestamp

        if (cacheInvalid) {
            Log.d("DEBUG_ME", "Pembayaran cache is invalid! Purging local data now.")
            localPembayaranDataSource.deleteAll()
                .onFailure {
                    Log.d("DEBUG_ME", "FAILED attempt to Invalidate/Purge Pembayaran Local : ${it.message}")
                }
            localMetadata.insert(MetadataModel(metadataTable, remoteTimestamp))
        }
    }

    private suspend fun updateMetadata() {
        val currentTimemillis = System.currentTimeMillis()
        val oldMetadata = localMetadata.get(metadataTable) ?: MetadataModel(metadataTable, 0L)
        val newMetadata = MetadataModel(metadataTable, currentTimemillis)

        localMetadata.insert(newMetadata)
        remoteMetadata.update(oldMetadata, newMetadata)
    }

    override fun deleteAllPembayaran(kavlingKode: String): Flow<Result<Boolean>> {
        return flow {
            // delete both from local and remote
            val localResult = localPembayaranDataSource.deleteAllPembayaranModel(kavlingKode)
            localResult.onFailure {
                emit(Result.failure(it))
            }

            val remoteResult = remotePembayaranSource.deleteAllPembayaranModel(kavlingKode)
            remoteResult.onSuccess {
                emit(Result.success(true))
            }
            remoteResult.onFailure {
                emit(Result.failure(it))
            }
        }
    }

}