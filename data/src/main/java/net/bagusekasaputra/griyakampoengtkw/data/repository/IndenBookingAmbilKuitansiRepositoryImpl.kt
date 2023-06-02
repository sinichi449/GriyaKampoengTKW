package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.util.Log
import net.bagusekasaputra.griyakampoengtkw.data.CacheHelper
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalIndenBookingAmbilKuitansiDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteIndenBookingAmbilKuitansiDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.entity.IndenBookingAmbilKuitansi
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingAmbilKuitansiRepository

class IndenBookingAmbilKuitansiRepositoryImpl(
    private val localDataSource: LocalIndenBookingAmbilKuitansiDataSource,
    private val remoteDataSource: RemoteIndenBookingAmbilKuitansiDataSource,
    private val cacheHelper: CacheHelper,
): IndenBookingAmbilKuitansiRepository {

    private val cacheLocalTable = "ambilKuitansiIndenBooking"
    private val cacheRemoteTable = "indenBooking/ambilKuitansi"

    override suspend fun get(keyId: String, termin: String): Result<IndenBookingAmbilKuitansi?> {
        return try {
            val isInvalidCache = cacheHelper.checkAndInvalidateCache(
                cacheLocalTable, cacheRemoteTable,
                onInvalid = {
                    localDataSource.deleteAll()
                }
            )
            val localModel = localDataSource.get(keyId, termin).getOrThrow()
            val ambilKuitansi: IndenBookingAmbilKuitansi? =
                if (isInvalidCache || (localModel == null)) {
                    Log.d("INDEN_BOOKING", "Ambil Kuitansi $termin cache is invalid or Local Data Source was returning NULL! " +
                            "Fetching from Remote Data Source now ...")

                    val remoteModel = remoteDataSource.get(keyId, termin).getOrThrow()
                    if (remoteModel != null) {
                        localDataSource.insert(remoteModel)

                        // Second try
                        localDataSource.get(keyId, termin).getOrThrow()?.let {
                            Log.d("INDEN_BOOKING", "Ambil kuitansi $termin successfully fetched from Remote Data Source and inserted to Local Data Source!")
                            MyObjectMapper.mapIndenBookingAmbilKuitansi(it)
                        }
                    } else {
                        Log.d("INDEN_BOOKING", "Ambil kuitansi $termin is NOT FOUND in Remote Data Source!")

                        null
                    }
                } else {
                    Log.d("INDEN_BOOKING", "Ambil kuitansi $termin on Local Data source is OK!")

                    MyObjectMapper.mapIndenBookingAmbilKuitansi(localModel)
                }

            Result.success(ambilKuitansi)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun insert(ambilKuitansi: IndenBookingAmbilKuitansi): Result<Nothing?> {
        TODO("Not yet implemented")
    }
}