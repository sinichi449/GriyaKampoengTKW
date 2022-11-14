package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingModel
import net.bagusekasaputra.griyakampoengtkw.data.source.local.kavling.LocalKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.kavling.RemoteKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes.Companion.LOG_TAG
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KavlingRepositoryImpl @Inject constructor(
    private val localKavlingRepository: LocalKavlingRepository,
    private val remoteKavlingRepository: RemoteKavlingRepository
): KavlingRepository {

    override fun getKavlingByBlock(blockCode: String): Flow<Result<List<Kavling>>> {
        return flow<Result<List<Kavling>>> {
            // Getting kavling from server first
            Log.d(LOG_TAG, "Getting kavling from server ...")
            val getKavlingFromRemote = remoteKavlingRepository.getAllKavlings(blockCode)

            if (getKavlingFromRemote.isSuccess) {
                // Emit the kavling
                emit(mapKavling(getKavlingFromRemote))

                // Then write kavling to local
                val kavlingModels = getKavlingFromRemote.getOrNull()?.map { mapKavling(it) }

                kavlingModels?.forEach {
                    localKavlingRepository.addKavling(blockCode, mapKavling(it))
                }
            } else {
                // Emit the error
                getKavlingFromRemote.exceptionOrNull()?.let { emit(Result.failure(it)) }

                // Emit kavling from local
                Log.d(LOG_TAG, "Getting kavling from local ...")
                val getKavlingFromLocal = localKavlingRepository.getKavlingByBlockKode(blockCode)

                if (getKavlingFromLocal.isSuccess) {
                    emit(mapKavling(getKavlingFromLocal))
                } else {
                    emit(Result.failure(UnknownError("Getting kavling from both server and local failed")))
                }
            }
        }
    }

    private fun mapKavling(result: Result<List<KavlingModel>?>): Result<List<Kavling>> {
        return result.map { kavlingModels ->
            kavlingModels?.map {
                mapKavling(it)
            } ?: emptyList()
        }
    }

    override fun addKavling(blockKode: String, kavling: Kavling): Flow<Result<Boolean>> {
        return flow {
            emitAll(
                remoteKavlingRepository.addKavling(blockKode, mapKavling(kavling))
            )
        }
    }

    override fun editKavling(
        blockCode: String,
        oldKavling: Kavling,
        newKavling: Kavling
    ): Flow<Result<Boolean>> {
        val firstKavlingModel = mapKavling(oldKavling)
        val secondKavlingModel = mapKavling(newKavling)

        return flow {
            emitAll(
                remoteKavlingRepository.editKavling(blockCode, firstKavlingModel, secondKavlingModel)
            )
        }
    }

    override fun removeKavling(blockCode: String, kavlingKode: String): Flow<Result<Boolean>> {
        return flow {
            emitAll(
                remoteKavlingRepository.removeKavling(blockCode, kavlingKode)
            )
        }
    }

    private fun mapKavling(kavlingModel: KavlingModel): Kavling {
        return Kavling(
            kavlingModel.kode,
            kavlingModel.active,
            kavlingModel.warna,
            kavlingModel.ukuran,
            kavlingModel.type
        )
    }

    private fun mapKavling(kavling: Kavling): KavlingModel {
        return KavlingModel(
            kavling.kode,
            kavling.warna,
            kavling.belumIsi,
            kavling.ukuran,
            kavling.type
        )
    }

}