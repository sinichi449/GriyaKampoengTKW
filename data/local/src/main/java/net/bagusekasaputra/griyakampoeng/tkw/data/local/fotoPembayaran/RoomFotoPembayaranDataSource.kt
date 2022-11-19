package net.bagusekasaputra.griyakampoeng.tkw.data.local.fotoPembayaran

import android.net.Uri
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalFotoPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranModel

class RoomFotoPembayaranDataSource(
    roomDatabase: MyRoomDatabase,
): LocalFotoPembayaranDataSource {

    private val fotoPembayaranDao = roomDatabase.getFotoPembayaranDao()

    override suspend fun getFotoPembayaran(
        kavlingKode: String,
        termin: String
    ): Result<FotoPembayaranModel?> {
        return RoomRequestHelper.doGetOperation {
            val fotoPembayaran = fotoPembayaranDao.getFotoPembayaran(kavlingKode, termin)

            if (fotoPembayaran != null)
                return@doGetOperation mapFotoPembayaranEntity(fotoPembayaran)
            else
                return@doGetOperation null
        }
    }

    override suspend fun addFotoPembayaran(fotoPembayaranModel: FotoPembayaranModel): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            val mapToEntity = mapFotoPembayaranEntity(fotoPembayaranModel)

            fotoPembayaranDao.insert(mapToEntity)
        }
    }

    override suspend fun deleteById(id: Long): Result<Nothing?> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteByKavlingKodeAndTermin(
        kavlingKode: String,
        termin: String
    ): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            fotoPembayaranDao.deleteByKavlingKodeAndTermin(kavlingKode, termin)
        }
    }

    override suspend fun deleteAllFotoPembayaran(kavlingKode: String): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            fotoPembayaranDao.deleteAllInKavling(kavlingKode)
        }
    }

    override suspend fun updateFotoPembayaran(
        oldFotoPembayaranModel: FotoPembayaranModel,
        newFotoPembayaranModel: FotoPembayaranModel
    ): Result<Nothing?> {
        TODO("Not yet implemented")
    }

    override suspend fun getFotoUri(kavlingKode: String, termin: String): Result<Uri?> {
        TODO("Not yet implemented")
    }

    private fun mapFotoPembayaranEntity(fotoPembayaranEntity: FotoPembayaranEntity): FotoPembayaranModel {
        return fotoPembayaranEntity.let {
            FotoPembayaranModel(
                id = it.id,
                kavlingKode = it.kavlingKode,
                termin = it.termin,
                uriStr = it.uriStr,
            )
        }
    }

    private fun mapFotoPembayaranEntity(fotoPembayaranModel: FotoPembayaranModel): FotoPembayaranEntity {
        return fotoPembayaranModel.let {
            FotoPembayaranEntity(
                id = it.id,
                kavlingKode = it.kavlingKode,
                termin = it.termin,
                uriStr = it.uriStr,
            )
        }
    }
}