package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel
import net.bagusekasaputra.griyakampoengtkw.data.source.local.datadiri.LocalDataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.datadiri.RemoteDataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.kavling.RemoteKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataDiriRepositoryImpl @Inject constructor(
    private val localDataDiriRepository: LocalDataDiriRepository,
    private val remoteDataDiriRepository: RemoteDataDiriRepository,
    private val remoteKavlingRepository: RemoteKavlingRepository,
): DataDiriRepository {

    override fun getDataDiri(kavlingKode: String): Flow<Result<DataDiri?>> {
        return flow {
            // First, get from remote server.
            val getDataDiriRemote = remoteDataDiriRepository.getDataDiri(kavlingKode)

            if (getDataDiriRemote.isSuccess) {
                // Emit the data diri
                emit(DataUtil.mapSingleResult(getDataDiriRemote, ::mapDataDiri))

                // Then save to local
                getDataDiriRemote.getOrNull()?.let {
                    localDataDiriRepository.addDataDiri(kavlingKode, it)
                }
            } else {
                // Emit the error
                getDataDiriRemote.exceptionOrNull()?.let { emit(Result.failure(it)) }

                // Emit from local instead
                val getDataDiriFromLocal = localDataDiriRepository.getDataDiri(kavlingKode)

                if (getDataDiriFromLocal.isSuccess)
                    emit(DataUtil.mapSingleResult(getDataDiriFromLocal, ::mapDataDiri))
                else
                    emit(Result.failure(UnknownError("Gagal mendapatkan data diri")))
            }
        }
    }

    override fun addDataDiri(kavlingKode: String, dataDiri: DataDiri): Flow<Result<Boolean>> {
        return flow {
            val model = mapDataDiri(dataDiri)

            // Whenever data diri added, let the kavling set "sudah Isi Data Diri"
            remoteKavlingRepository.setKavlingBelumDiisi(kavlingKode, false)

            emitAll(remoteDataDiriRepository.addDataDiri(kavlingKode, model))
        }
    }

    override fun deleteDataDiri(kavlingKode: String): Flow<Result<Boolean>> {
        return flow {
            // Whenever data diri deleted, let kavling "sudah isi Data Diri" to be false
            remoteKavlingRepository.setKavlingBelumDiisi(kavlingKode, true)

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