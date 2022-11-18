package net.bagusekasaputra.griyakampoengtkw.data.source.local.fotoPembayaran.room

import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.source.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoengtkw.data.source.local.RoomRequestHelper
import net.bagusekasaputra.griyakampoengtkw.data.source.local.fotoPembayaran.LocalFotoPembayaranDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomFotoPembayaranDataSource @Inject constructor(
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

    override suspend fun updateFotoPembayaran(
        oldFotoPembayaranModel: FotoPembayaranModel,
        newFotoPembayaranModel: FotoPembayaranModel
    ): Result<Nothing?> {
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