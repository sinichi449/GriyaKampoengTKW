package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteDataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository

class DataDiriRepositoryImpl(
    private val localDataDiriDataSource: LocalDataDiriDataSource,
    private val remoteDataDiriRepository: RemoteDataDiriRepository,
    private val remoteKavlingDataSource: RemoteKavlingDataSource,
): DataDiriRepository {

    override fun getDataDiri(kavlingKode: String, offline: Boolean): Flow<Result<DataDiri?>> {
        return flow {
            val flowOffline = flow<Result<DataDiri?>> {
                val getDataDiriFromLocal = localDataDiriDataSource.getDataDiri(kavlingKode)

                if (getDataDiriFromLocal.isSuccess)
                    emit(DataUtil.mapSingleResult(getDataDiriFromLocal, ::mapDataDiri))
                else
                    emit(Result.failure(Throwable("Error tak diketahui")))
            }

            val flowOnline = flow<Result<DataDiri?>> {
                // Get from remote
                val getDataDiriRemote = remoteDataDiriRepository.getDataDiri(kavlingKode)

                if (getDataDiriRemote.isSuccess) {
                    // Emit the data diri
                    emit(DataUtil.mapSingleResult(getDataDiriRemote, ::mapDataDiri))

                    // Then save to local
                    getDataDiriRemote.getOrNull()?.let {
                        localDataDiriDataSource.addDataDiri(kavlingKode, it)
                    }
                } else {
                    // Emit the error
                    getDataDiriRemote.exceptionOrNull()?.let { emit(Result.failure(it)) }

                    // Emit from local instead
                    emitAll(flowOffline)
                }
            }

            if (offline)
                emitAll(flowOffline)
            else
                emitAll(flowOnline)
        }
    }

    override fun addDataDiri(kavlingKode: String, dataDiri: DataDiri): Flow<Result<Boolean>> {
        return flow {
            val model = mapDataDiri(dataDiri)

            // Whenever data diri added, let the kavling set "sudah Isi Data Diri"
            remoteKavlingDataSource.setKavlingBelumDiisi(kavlingKode, false)

            // Adding mechanism on Local Data Source already available on getDataDiri() method.

            emitAll(remoteDataDiriRepository.addDataDiri(kavlingKode, model))
        }
    }

    override fun deleteDataDiri(kavlingKode: String): Flow<Result<Boolean>> {
        return flow {
            // Whenever data diri deleted, let kavling "sudah isi Data Diri" to be false
            remoteKavlingDataSource.setKavlingBelumDiisi(kavlingKode, true)

            // Also, delete the data diri on Local!
            val deleteLocal = localDataDiriDataSource.deleteDataDiri(kavlingKode)
            deleteLocal.onFailure {
                emit(Result.failure(it))
            }

            // Then, delete on the remote ...
            emitAll(remoteDataDiriRepository.deleteDataDiri(kavlingKode))
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