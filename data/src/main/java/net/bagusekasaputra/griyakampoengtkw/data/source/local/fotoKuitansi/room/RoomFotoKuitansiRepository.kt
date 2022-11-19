package net.bagusekasaputra.griyakampoengtkw.data.source.local.fotoKuitansi.room

import net.bagusekasaputra.griyakampoengtkw.data.model.FotoKuitansiModel
import net.bagusekasaputra.griyakampoengtkw.data.source.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoengtkw.data.source.local.fotoKuitansi.LocalFotoKuitansiRepository

class RoomFotoKuitansiRepository(
    roomDatabase: MyRoomDatabase,
): LocalFotoKuitansiRepository {

    private val fotoKuitansiDao = roomDatabase.getFotoKuitansiDao()

    override suspend fun getFotoKuitansi(kavlingKode: String): Result<FotoKuitansiModel?> {
        return try {
            val fotoKuitansiEntity = fotoKuitansiDao.getByKavlingKode(kavlingKode)

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