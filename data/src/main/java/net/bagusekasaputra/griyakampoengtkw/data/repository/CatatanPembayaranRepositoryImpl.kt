package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.CatatanPembayaranModel
import net.bagusekasaputra.griyakampoengtkw.domain.entity.CatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.CatatanPembayaranRepository

class CatatanPembayaranRepositoryImpl(
    private val remoteCatatanPembayaranDataSource: RemoteCatatanPembayaranDataSource,
): CatatanPembayaranRepository {

    override fun getCatatan(kavlingKode: String): Flow<Result<CatatanPembayaran?>> {
        return flow {
            val remoteResult = remoteCatatanPembayaranDataSource.getCatatan(kavlingKode)
            val mappedResult = DataUtil.mapSingleResult(
                originResult = remoteResult,
                targetMapper = ::mapCatatanPembayaran,
            )

            emit(mappedResult)
        }
    }

    override fun addCatatan(
        kavlingKode: String,
        catatanPembayaran: CatatanPembayaran
    ): Flow<Result<Nothing?>> {
        return flow {
            val remoteResult = remoteCatatanPembayaranDataSource.addCatatan(
                kavlingKode = kavlingKode,
                catatanPembayaranModel = mapCatatanPembayaran(catatanPembayaran),
            )

            emit(remoteResult)
        }
    }

    override fun deleteCatatan(kavlingKode: String): Flow<Result<Nothing?>> {
        return flow {
            val remoteResult = remoteCatatanPembayaranDataSource.deleteCatatan(kavlingKode)

            emit(remoteResult)
        }
    }

    private fun mapCatatanPembayaran(catatanPembayaranModel: CatatanPembayaranModel): CatatanPembayaran {
        return catatanPembayaranModel.let {
            CatatanPembayaran(
                kavlingKode = it.kavlingKode,
                content = it.content,
            )
        }
    }

    private fun mapCatatanPembayaran(catatanPembayaran: CatatanPembayaran): CatatanPembayaranModel {
        return catatanPembayaran.let {
            CatatanPembayaranModel(
                kavlingKode = it.kavlingKode,
                content = it.content,
            )
        }
    }
}