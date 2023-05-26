package net.bagusekasaputra.griyakampoengtkw.data.repository

import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.HargaRumahIndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingRepository

class IndenBookingRepositoryImpl(
    private val localDataSource: LocalIndenBookingDataSource,
    private val remoteDataSource: RemoteIndenBookingDataSource,
    private val localMetadata: LocalMetadataDataSource,
    private val remoteMetadata: RemoteMetadataDataSource,
): IndenBookingRepository {

    override suspend fun getAllKeyIds(dataMode: DataMode): Result<List<String>?> {
        return remoteDataSource.getAllKeyIds()
    }

    override suspend fun getHargaRumah(
        keyId: String,
        dataMode: DataMode
    ): Result<HargaRumahIndenBooking?> {
        val remoteResult = remoteDataSource.getHargaRumah(keyId)

        return DataUtil.mapSingleResult(
            originResult = remoteResult,
            targetMapper = MyObjectMapper::mapHargaRumah,
        )
    }

}