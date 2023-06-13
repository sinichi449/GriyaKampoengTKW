package net.bagusekasaputra.griyakampoeng.tkw.data.local.sources

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.KavlingCatatanPembayaranRoomEntity
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalKavlingCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingCatatanPembayaranModel

class RoomKavlingCatatanPembayaranDataSource(
    roomDatabase: MyRoomDatabase
): LocalKavlingCatatanPembayaranDataSource {

    private val catatanPembayaranDao = roomDatabase.getCatatanPembayaranDao()

    override suspend fun getCatatan(kavlingKode: String): Result<KavlingCatatanPembayaranModel?> {
        return RoomRequestHelper.doGetOperation {
            catatanPembayaranDao.getCatatan(kavlingKode).let {
                if (it != null)
                    KavlingCatatanPembayaranModel(
                        kavlingKode = it.kavlingKode,
                        content = it.content,
                    )
                else
                    null
            }
        }
    }

    override suspend fun addCatatan(
        kavlingKode: String,
        kavlingCatatanPembayaranModel: KavlingCatatanPembayaranModel
    ): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            // Check if exist
            val isExist = (catatanPembayaranDao.getCatatan(kavlingKode) != null)
            if (isExist)
                // If exist, update instead
                catatanPembayaranDao.updateCatatan(kavlingCatatanPembayaranModel.kavlingKode, kavlingCatatanPembayaranModel.content)
            else
                catatanPembayaranDao.addCatatan(
                    KavlingCatatanPembayaranRoomEntity(
                        kavlingKode = kavlingCatatanPembayaranModel.kavlingKode,
                        content = kavlingCatatanPembayaranModel.content,
                    )
                )
        }
    }

    override suspend fun deleteCatatan(kavlingKode: String): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            catatanPembayaranDao.deleteCatatan(kavlingKode)
        }
    }

    override suspend fun updateCatatan(
        kavlingKode: String,
        oldData: KavlingCatatanPembayaranModel,
        newData: KavlingCatatanPembayaranModel
    ): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            catatanPembayaranDao.updateCatatan(newData.kavlingKode, newData.content)
        }
    }
}