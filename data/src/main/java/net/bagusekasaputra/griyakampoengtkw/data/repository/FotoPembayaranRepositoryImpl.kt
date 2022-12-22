package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalFotoPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteFotoPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.model.MetadataModel
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoPembayaranRepository

class FotoPembayaranRepositoryImpl(
    private val localFotoPembayaran: LocalFotoPembayaranDataSource,
    private val deviceDataSource: LocalFotoPembayaranDataSource,
    private val remoteFotoPembayaran: RemoteFotoPembayaranDataSource,
    private val localMetadata: LocalMetadataDataSource,
    private val remoteMetadata: RemoteMetadataDataSource,
): FotoPembayaranRepository {

    private val localTable = { kavlingKode: String ->
        "${kavlingKode}_image_foto_pembayaran"
    }
    private val remoteTable = { kavlingKode: String ->
        "image_foto_pembayaran/${kavlingKode}/"
    }

    override fun getFotoPembayaran(
        kavlingKode: String,
        termin: String
    ): Flow<Result<FotoPembayaran?>> {
        return flow {
            // Cache validation
            val localTimestamp = localMetadata.get(localTable(kavlingKode))
                ?.timestamp
            val serverTimestamp = remoteMetadata.get(remoteTable(kavlingKode))!!
                .timestamp
            val cacheInvalid = localTimestamp != serverTimestamp

            if (cacheInvalid) {
                Log.d("DEBUG_ME", "FotoPembayaranRepo->get(): Cache foto pembayaran invalid!! Deleting all foto pembayaran cache ...")
                localFotoPembayaran.deleteAll(kavlingKode)
                localMetadata.insert(
                    MetadataModel(localTable(kavlingKode), serverTimestamp)
                )
            }

            // Emiting value
            Log.d("DEBUG_ME", "FotoPembayaranRepo->get(): Cache for foto pembayaran is okay, getting from local data source ...")
            val localModel = localFotoPembayaran.getFotoPembayaran(kavlingKode, termin)
                .getOrNull()
            if (localModel == null) {
                Log.d("DEBUG_ME", "FotoPembayaranRepo->get(): Local data source Foto Pembayaran is null, getting \"$kavlingKode\" from remote ...")
                remoteFotoPembayaran.get(kavlingKode, termin)?.let { remoteModel ->
                    Log.d("DEBUG_ME", "FotoPembayaranRepo->get(): Uri from remote data source is ${remoteModel.uriStr}")
                    localFotoPembayaran.addFotoPembayaran(remoteModel, true)
                        .onSuccess {
                            Log.d("DEBUG_ME", "Successfully inserting foto pembayaran ${remoteModel.uriStr} to local data source.")
                        }
                        .onFailure {
                            Log.d("DEBUG_ME", "Fails to insert foto pembayaran to ${remoteModel.kavlingKode} in termin ${remoteModel.termin}: ${it.message}")
                        }
                }

                // Second try
                emit(localFotoPembayaran.getFotoPembayaran(kavlingKode, termin).map { model ->
                    if (model != null) mapFotoPembayaranModel(model)
                    else null
                })
            } else {
                emit(Result.success(mapFotoPembayaranModel(localModel)))
                Log.d("DEBUG_ME", "FotoPembayaranRepoImpl->get(): Succesfully fetch Foto Pembayaran $kavlingKode on termin $termin from local data source.")
            }
        }
    }

    override fun addFotoPembayaran(
        kavlingKode: String,
        termin: String,
        fotoPembayaran: FotoPembayaran
    ): Flow<Result<Nothing?>> {
        return flow {
            updateMetadata(kavlingKode)

            val newModel = FotoPembayaranModel(
                kavlingKode = kavlingKode,
                termin = termin,
                uriStr = fotoPembayaran.uri.toString(),
            )

            localFotoPembayaran.addFotoPembayaran(newModel, false)
                .onSuccess {
                    Log.d("DEBUG_ME", "FotoPembayaranRepoImpl->addFotoPembayaran(): Success adding foto pembayaran at $kavlingKode on termin $termin")
                }
                .onFailure {
                    Log.d("DEBUG_ME", "FotoPembayaranRepoImpl->addFotoPembayaran(): Error : ${it.message}")
                }

            emitAll(remoteFotoPembayaran.insert(newModel))
        }
    }

    override fun deleteFotoPembayaran(kavlingKode: String, termin: String): Flow<Result<Nothing?>> {
        return callbackFlow {
//            // Deleting both in the Device and in the Room Database.
//            val deviceResult = deviceDataSource.deleteByKavlingKodeAndTermin(kavlingKode, termin)
//
//            deviceResult.onFailure {
//                emit(Result.failure(it))
//            }
//
//            val localResult = localFotoPembayaran.deleteByKavlingKodeAndTermin(kavlingKode, termin)
//
//            emit(localResult)
            updateMetadata(kavlingKode)

            remoteFotoPembayaran.delete(kavlingKode, termin)
                .onFailure {
                    trySendBlocking(Result.failure(it))
                }

            localFotoPembayaran.deleteByKavlingKodeAndTermin(kavlingKode, termin)
                .onSuccess {
                    trySendBlocking(Result.success(null))
                }
                .onFailure {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }
    }

    override fun isFotoPembayaranExist(kavlingKode: String, termin: String): Flow<Result<Boolean>> {
        return flow {
            emitAll(remoteFotoPembayaran.isFotoPembayaranExist(kavlingKode, termin))
        }
    }

    override fun deleteAllFotoPembayaran(kavlingKode: String): Flow<Result<Nothing?>> {
        return flow {
            // Deleting both in the Device storage and in the Room Database
            val deviceResult = deviceDataSource.deleteAllFotoPembayaran(kavlingKode)

            deviceResult.onFailure {
                emit(Result.failure(it))
            }

            val localResult = localFotoPembayaran.deleteAllFotoPembayaran(kavlingKode)

            emit(localResult)
        }
    }

    private fun mapFotoPembayaranModel(fotoPembayaran: FotoPembayaran, uriStr: String): FotoPembayaranModel {
        return fotoPembayaran.let {
            FotoPembayaranModel(
                id = it.id,
                kavlingKode = it.kavlingKode,
                termin = it.termin,
                uriStr = uriStr,
            )
        }
    }

    private fun mapFotoPembayaranModel(fotoPembayaranModel: FotoPembayaranModel): FotoPembayaran {
        return fotoPembayaranModel.let {
            FotoPembayaran(
                id = it.id,
                kavlingKode = it.kavlingKode,
                termin = it.termin,
                uri = it.getUri(),
            )
        }
    }

    private suspend fun updateMetadata(kavlingKode: String) {
        Log.d("DEBUG_ME", "FotoPembayaranRepoImpl->updateMetadata(): Updating foto pembayaran $kavlingKode metadata ...")
        val currentTimemillis = System.currentTimeMillis()
        val oldTimestamp = localMetadata.get(localTable(kavlingKode))?.timestamp ?: 0L

        remoteMetadata.update(
            oldMetadataModel = MetadataModel(remoteTable(kavlingKode), oldTimestamp),
            newMetadataModel = MetadataModel(remoteTable(kavlingKode), currentTimemillis),
        )
        localMetadata.insert(
            MetadataModel(localTable(kavlingKode), currentTimemillis)
        )

        Log.d("DEBUG_ME", "FotoPembayaranRepoImpl->updateMetadata(): Metadata foto pembayaran for $kavlingKode successfully updated. ")
    }
}