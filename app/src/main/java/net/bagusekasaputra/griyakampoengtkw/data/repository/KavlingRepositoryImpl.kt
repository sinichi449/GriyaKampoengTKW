package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.source.model.KavlingModel
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.kavling.RemoteKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KavlingRepositoryImpl @Inject constructor(
    private val remoteKavlingRepository: RemoteKavlingRepository
): KavlingRepository {

    override fun getKavlingByBlock(blockCode: String): Flow<Result<List<Kavling>>> {
        return flow {
            val flowResultKavlingModel = remoteKavlingRepository.getAllKavlings(blockCode)
            flowResultKavlingModel.collect { result ->
                if (result.isSuccess) {
                    val kavlings = result.getOrNull()?.map { kavlingModel ->
                        mapKavling(kavlingModel)
                    }
                    kavlings?.let {
                        emit(Result.success(it))
                    }
                } else {
                    val throwable = result.exceptionOrNull()
                    throwable?.let {
                        emit(Result.failure(it))
                    }
                }
            }
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
            kavlingModel.isActive,
            kavlingModel.warna,
            kavlingModel.ukuran,
            kavlingModel.type
        )
    }

    private fun mapKavling(kavling: Kavling): KavlingModel {
        return KavlingModel(
            kavling.kode,
            kavling.warna,
            kavling.isActive,
            kavling.ukuran,
            kavling.type
        )
    }

}