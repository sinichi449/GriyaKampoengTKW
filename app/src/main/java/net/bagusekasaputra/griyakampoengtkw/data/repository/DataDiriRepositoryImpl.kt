package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.source.model.DataDiriModel
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.datadiri.RemoteDataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataDiriRepositoryImpl @Inject constructor(
    private val remoteDataDiriRepository: RemoteDataDiriRepository
): DataDiriRepository {

    override fun getDataDiri(kavlingKode: String): Flow<Result<DataDiri?>> {
        return flow {
            val flowDataDiriModel = remoteDataDiriRepository.getDataDiri(kavlingKode)

            flowDataDiriModel.collect { resultModel ->
                if (resultModel.isSuccess) {
                    val model = resultModel.getOrNull()

                    if (model != null) emit(Result.success(mapDataDiri(model)))
                    else emit(Result.success(null))
                } else {
                    resultModel.exceptionOrNull()?.let {
                        emit(Result.failure(it))
                    }
                }
            }
        }
    }

    override fun addDataDiri(kavlingKode: String, dataDiri: DataDiri): Flow<Result<Boolean>> {
        return flow {
            val model = mapDataDiri(dataDiri)

            emitAll(remoteDataDiriRepository.addDataDiri(kavlingKode, model))
        }
    }

    override fun deleteDataDiri(kavlingKode: String): Flow<Result<Boolean>> {
        return flow {
            remoteDataDiriRepository.deleteDataDiri(kavlingKode).collect {
                emit(it)
            }
        }
    }


    private fun mapDataDiri(dataDiriModel: DataDiriModel): DataDiri {
        return dataDiriModel.let {
            DataDiri(
                nama = it.nama,
                jenisIdentitas = it.jenisIdentitas,
                noIdentitas = it.noIdentitas,
                negaraBekerja = it.negaraBekerja,
                alamatIndo = it.alamatIndo,
                alamatKerja = it.alamatKerja,
                noHp = it.noHp
            )
        }
    }

    private fun mapDataDiri(dataDiri: DataDiri): DataDiriModel {
        return dataDiri.let {
            DataDiriModel(
                nama = it.nama,
                jenisIdentitas = it.jenisIdentitas,
                noIdentitas = it.noIdentitas,
                alamatKerja = it.alamatKerja,
                negaraBekerja = it.negaraBekerja,
                alamatIndo = it.alamatIndo,
                noHp = it.noHp
            )
        }
    }

}