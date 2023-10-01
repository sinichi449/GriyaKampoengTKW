package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.util.Log
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteTambahanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.TambahanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.TambahanPembayaranRepository

class TambahanPembayaranRepositoryImpl(
    private val remoteDataSource: RemoteTambahanPembayaranDataSource,
): TambahanPembayaranRepository {

    override suspend fun getAllByKavling(kavling: String): Result<List<TambahanPembayaran>?> {
        Log.d("TAMBAHAN_PEMBAYARAN", "Request for kavling: $kavling")
        return remoteDataSource.getAll(kavling).map { models ->
            models?.map {
                MyObjectMapper.mapTambahanPembayaran(it)
            }
        }
    }

    override suspend fun getById(kavling: String, id: String): Result<TambahanPembayaran?> {
        return remoteDataSource.getById(kavling, id).map { model ->
            model?.let {
                MyObjectMapper.mapTambahanPembayaran(it)
            }
        }
    }

    override suspend fun insert(tambahanPembayaran: TambahanPembayaran): Result<Nothing?> {
        val model = MyObjectMapper.mapTambahanPembayaran(tambahanPembayaran)

        return remoteDataSource.insert(model)
    }

    override suspend fun update(
        kavling: String,
        id: String,
        newData: TambahanPembayaran
    ): Result<Nothing?> {
        val newModel = MyObjectMapper.mapTambahanPembayaran(newData)

        return remoteDataSource.update(kavling, id, newModel)
    }
}