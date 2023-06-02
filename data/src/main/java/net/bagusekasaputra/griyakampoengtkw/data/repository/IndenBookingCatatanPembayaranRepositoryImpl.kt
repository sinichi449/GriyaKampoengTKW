package net.bagusekasaputra.griyakampoengtkw.data.repository

import net.bagusekasaputra.griyakampoengtkw.data.CacheHelper
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalIndenBookingCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteIndenBookingCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.entity.catatanPembayaran.IndenBookingCatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingCatatanPembayaranRepository

class IndenBookingCatatanPembayaranRepositoryImpl(
    private val localDataSource: LocalIndenBookingCatatanPembayaranDataSource,
    private val remoteDataSource: RemoteIndenBookingCatatanPembayaranDataSource,
    private val cacheHelper: CacheHelper,
): IndenBookingCatatanPembayaranRepository {

    private val cacheLocalTable = "catatanPembayaranIndenBooking"
    private val cacheRemoteTable = "indenBooking/catatanPembayaran"

    override suspend fun get(keyId: String): Result<IndenBookingCatatanPembayaran?> {
        return try {
            val isInvalidCache = cacheHelper.checkAndInvalidateCache(
                cacheLocalTable, cacheRemoteTable,
                onInvalid = {
                    localDataSource.deleteAll()
                }
            )
            val localModel = localDataSource.get(keyId).getOrThrow()

            val catatanPembayaran =
                if (isInvalidCache || (localModel == null)) {
                    val remoteModel = remoteDataSource.get(keyId).getOrThrow()
                    if (remoteModel != null) {
                        localDataSource.insert(remoteModel)

                        localDataSource.get(keyId).getOrThrow()?.let {
                            MyObjectMapper.mapIndenBookingCatatanPembayaran(it)
                        }
                    } else {
                        null
                    }
                } else {
                    MyObjectMapper.mapIndenBookingCatatanPembayaran(localModel)
                }

            Result.success(catatanPembayaran)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}