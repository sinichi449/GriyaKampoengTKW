package net.bagusekasaputra.griyakampoengtkw.data.source.local.fotoKuitansi.room

import net.bagusekasaputra.griyakampoengtkw.data.model.FotoKuitansiModel
import net.bagusekasaputra.griyakampoengtkw.data.source.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoengtkw.data.source.local.fotoKuitansi.LocalFotoKuitansiRepository
import net.bagusekasaputra.griyakampoengtkw.logEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomFotoKuitansiRepository @Inject constructor(
    roomDatabase: MyRoomDatabase,
): LocalFotoKuitansiRepository {

    private val fotoKuitansiDao = roomDatabase.getFotoKuitansiDao()

    override suspend fun getFotoKuitansi(kavlingKode: String): Result<FotoKuitansiModel?> {
        return try {
            logEvent("Getting foto kuitansi from room ...")
            val fotoKuitansiEntity = fotoKuitansiDao.getByKavlingKode(kavlingKode)

            logEvent("Result from room -> ${fotoKuitansiEntity?.fotoUri}")

            if (fotoKuitansiEntity != null)
                Result.success(mapFotoKuitansiEntity(fotoKuitansiEntity))
            else
                Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun addFotoKuitansi(fotoKuitansiModel: FotoKuitansiModel): Result<Nothing?> {
        return try {
            val resultId = fotoKuitansiDao.insert(mapFotoKuitansiEntity(fotoKuitansiModel))

            logEvent("Added bukti kuitansi -> $resultId")

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    private fun mapFotoKuitansiEntity(fotoKuitansiEntity: FotoKuitansiRoomEntity): FotoKuitansiModel {
        return fotoKuitansiEntity.let {
            FotoKuitansiModel(
                kavlingKode = it.kavlingKode,
                fotoUri = it.fotoUri ?: "",
            )
        }
    }

    private fun mapFotoKuitansiEntity(fotoKuitansiModel: FotoKuitansiModel): FotoKuitansiRoomEntity {
        return fotoKuitansiModel.let {
            FotoKuitansiRoomEntity(
                kavlingKode = it.kavlingKode,
                fotoUri = it.fotoUri,
            )
        }
    }
}