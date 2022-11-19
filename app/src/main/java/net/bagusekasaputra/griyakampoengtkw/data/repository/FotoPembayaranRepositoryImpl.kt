package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.source.local.fotoPembayaran.LocalFotoPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.di.DataSourceModule
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.logEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FotoPembayaranRepositoryImpl @Inject constructor(
    @DataSourceModule.RoomDatabase val roomDataSource: LocalFotoPembayaranDataSource,
    @DataSourceModule.DeviceStorage val deviceDataSource: LocalFotoPembayaranDataSource,
): FotoPembayaranRepository {

    override fun getFotoPembayaran(
        kavlingKode: String,
        termin: String
    ): Flow<Result<FotoPembayaran?>> {
        return flow {
            // GET Operation doesn't need call to Device Storage.
            val localResult = roomDataSource.getFotoPembayaran(kavlingKode, termin)
            logEvent("Foto Kuitansi Uri -> ${localResult.getOrNull()?.uriStr}")
            val mappedResult = DataUtil.mapSingleResult(
                originResult = localResult,
                targetMapper = ::mapFotoPembayaranModel,
            )

            emit(mappedResult)
        }
    }

    override fun addFotoPembayaran(
        kavlingKode: String,
        termin: String,
        fotoPembayaran: FotoPembayaran
    ): Flow<Result<Nothing?>> {
        return flow {
            val uriStr = fotoPembayaran.uri.toString() // This is a dummy Uri, just ignore.
            var mapModel = mapFotoPembayaranModel(fotoPembayaran, uriStr)

            // First we need to copy the file to device storage.
            val deviceResult = deviceDataSource.addFotoPembayaran(mapModel)
            deviceResult.onFailure {
                emit(Result.failure(it))
            }

            // After copying the file to the device storage, we need the uri.
            val fileUriResult = deviceDataSource.getFotoUri(kavlingKode, termin)
            fileUriResult.onFailure {
                emit(Result.failure(it))
            }

            // Now, ready to be inserted into Room Database
            val fileUri = fileUriResult.getOrNull() ?: Uri.EMPTY
            mapModel = mapFotoPembayaranModel(fotoPembayaran, fileUri.toString())
            val localResult = roomDataSource.addFotoPembayaran(mapModel)

            emit(localResult)
        }
    }

    override fun deleteFotoPembayaran(kavlingKode: String, termin: String): Flow<Result<Nothing?>> {
        return flow {
            // Deleting both in the Device and in the Room Database.
            val deviceResult = deviceDataSource.deleteByKavlingKodeAndTermin(kavlingKode, termin)

            deviceResult.onFailure {
                emit(Result.failure(it))
            }

            val localResult = roomDataSource.deleteByKavlingKodeAndTermin(kavlingKode, termin)

            emit(localResult)
        }
    }

    override fun isFotoPembayaranExist(kavlingKode: String, termin: String): Flow<Result<Boolean>> {
        return flow {
            // We can take advantage of GET operation. Simply, if it returns null, then
            // Foto Pembayaran isn't exist.
            val getFotoPembayaran = roomDataSource.getFotoPembayaran(kavlingKode, termin)

            getFotoPembayaran.onSuccess { fotoPembayaran ->
                if (fotoPembayaran != null)
                    emit(Result.success(true))
                else
                    emit(Result.success(false))
            }

            getFotoPembayaran.onFailure {
                emit(Result.failure(getFotoPembayaran.exceptionOrNull() ?: UnknownError("ERROR: Gagal mengecek apakah Foto Pembayaran tersedia.")))
            }
        }
    }

    override fun deleteAllFotoPembayaran(kavlingKode: String): Flow<Result<Nothing?>> {
        return flow {
            // Deleting both in the Device storage and in the Room Database
            val deviceResult = deviceDataSource.deleteAllFotoPembayaran(kavlingKode)

            deviceResult.onFailure {
                emit(Result.failure(it))
            }

            val localResult = roomDataSource.deleteAllFotoPembayaran(kavlingKode)

            emit(localResult)
        }
    }

    private fun mapFotoPembayaranModel(fotoPembayaran: FotoPembayaran, uriStr: String): FotoPembayaranModel {
        return fotoPembayaran.let {
            FotoPembayaranModel(
                id = it.id,
                kavlingKode = it.kavlingKode,
                termin = it.termin,
                uriStr = uriStr,
            )
        }
    }

    private fun mapFotoPembayaranModel(fotoPembayaranModel: FotoPembayaranModel): FotoPembayaran {
        return fotoPembayaranModel.let {
            FotoPembayaran(
                id = it.id,
                kavlingKode = it.kavlingKode,
                termin = it.termin,
                uri = it.getUri(),
            )
        }
    }
}