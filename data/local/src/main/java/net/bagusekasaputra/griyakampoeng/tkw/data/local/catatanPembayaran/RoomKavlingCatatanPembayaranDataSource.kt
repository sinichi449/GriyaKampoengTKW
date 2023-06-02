package net.bagusekasaputra.griyakampoeng.tkw.data.local.catatanPembayaran

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalKavlingCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.CatatanPembayaranModel

class RoomKavlingCatatanPembayaranDataSource(
    roomDatabase: MyRoomDatabase
): LocalKavlingCatatanPembayaranDataSource {

    private val catatanPembayaranDao = roomDatabase.getCatatanPembayaranDao()

    override suspend fun getCatatan(kavlingKode: String): Result<CatatanPembayaranModel?> {
        return RoomRequestHelper.doGetOperation {
            catatanPembayaranDao.getCatatan(kavlingKode).let {
                if (it != null)
                    CatatanPembayaranModel(
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
        catatanPembayaranModel: CatatanPembayaranModel
    ): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            // Check if exist
            val isExist = (catatanPembayaranDao.getCatatan(kavlingKode) != null)
            if (isExist)
                // If exist, update instead
                catatanPembayaranDao.updateCatatan(catatanPembayaranModel.kavlingKode, catatanPembayaranModel.content)
            else
                catatanPembayaranDao.addCatatan(
                    CatatanPembayaranRoomEntity(
                        kavlingKode = catatanPembayaranModel.kavlingKode,
                        content = catatanPembayaranModel.content,
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
        oldData: CatatanPembayaranModel,
        newData: CatatanPembayaranModel
    ): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            catatanPembayaranDao.updateCatatan(newData.kavlingKode, newData.content)
        }
    }
}