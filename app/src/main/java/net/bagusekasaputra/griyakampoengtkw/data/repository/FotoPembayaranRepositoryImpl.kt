package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.source.local.fotoPembayaran.LocalFotoPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.logEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FotoPembayaranRepositoryImpl @Inject constructor(
    private val localFotoPembayaranDataSource: LocalFotoPembayaranDataSource,
): FotoPembayaranRepository {

    override fun getFotoPembayaran(
        kavlingKode: String,
        termin: String
    ): Flow<Result<FotoPembayaran?>> {
        return flow {
            val localResult = localFotoPembayaranDataSource.getFotoPembayaran(kavlingKode, termin)
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
            val uriStr = fotoPembayaran.uri.toString()
            val mapToModel = mapFotoPembayaranModel(fotoPembayaran, uriStr)

            val localResult = localFotoPembayaranDataSource.addFotoPembayaran(mapToModel)

            emit(localResult)
        }
    }

    override fun deleteFotoPembayaran(kavlingKode: String, termin: String): Flow<Result<Nothing?>> {
        return flow {
            val localResult = localFotoPembayaranDataSource.deleteByKavlingKodeAndTermin(kavlingKode, termin)

            emit(localResult)
        }
    }

    override fun isFotoPembayaranExist(kavlingKode: String, termin: String): Flow<Result<Boolean>> {
        return flow {
            // We can take advantage of GET operation. Simply, if it returns null, then
            // Foto Pembayaran isn't exist.
            val getFotoPembayaran = localFotoPembayaranDataSource.getFotoPembayaran(kavlingKode, termin)

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