package net.bagusekasaputra.griyakampoengtkw.data.repository

import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.HargaRumahIndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingRepository

class IndenBookingRepositoryImpl(
    private val remoteDataSource: RemoteIndenBookingDataSource
): IndenBookingRepository {

    override suspend fun getAllKeyIds(dataMode: DataMode): Result<List<String>?> {
        return remoteDataSource.getAllKeyIds()
    }

    override suspend fun getDataDiri(keyId: String, dataMode: DataMode): Result<DataDiri?> {
        val remoteResult = remoteDataSource.getDataDiri(keyId)

        return DataUtil.mapSingleResult(
            originResult = remoteResult,
            targetMapper = MyObjectMapper::mapDataDiri,
        )
    }

    override suspend fun getAllPembayaran(
        keyId: String,
        dataMode: DataMode
    ): Result<List<Pembayaran>?> {
        val remoteResult = remoteDataSource.getAllPembayaran(keyId)

        return DataUtil.mapListResult(
            originResult = remoteResult,
            targetMapper = MyObjectMapper::mapPembayaran,
        )
    }

    override suspend fun getHargaRumah(
        keyId: String,
        dataMode: DataMode
    ): Result<HargaRumahIndenBooking?> {
        // TODO
        return Result.success(null)
    }
}