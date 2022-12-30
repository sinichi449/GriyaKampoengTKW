package net.bagusekasaputra.griyakampoeng.tkw.data.local.hargaKavling

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalHargaKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaKavlingModel

class RoomHargaKavlingDataSource(
    private val roomDatabase: MyRoomDatabase
): LocalHargaKavlingDataSource {

    private val hargaKavlingDao = roomDatabase.getHargaKavlingDao()

    override suspend fun getHargaKavlingModel(kavlingKode: String): Result<HargaKavlingModel?> {
        return RoomRequestHelper.doGetOperation {
            hargaKavlingDao.getHargaKavling(kavlingKode).let {
                if (it != null)
                    HargaKavlingModel(
                        kavlingKode = it.kavlingKode,
                        harga = it.harga,
                        tambahLuasan = it.tambahLuasan,
                    )
                else
                    null
            }
        }
    }

    override suspend fun addHargaKavlingModel(hargaKavlingModel: HargaKavlingModel): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            // Check if exist
            val isExist = (hargaKavlingDao.getHargaKavling(hargaKavlingModel.kavlingKode)) != null

            if (isExist) {
                // If exist, use update operation
                hargaKavlingDao.updateHargaKavling(
                        kavlingKode = hargaKavlingModel.kavlingKode,
                        newHarga = hargaKavlingModel.harga,
                        newTambahLuasan = hargaKavlingModel.tambahLuasan,
                    )
            } else {
                hargaKavlingDao.insertHargaKavling(
                    HargaKavlingRoomEntity(
                        kavlingKode = hargaKavlingModel.kavlingKode,
                        harga = hargaKavlingModel.harga,
                        tambahLuasan = hargaKavlingModel.tambahLuasan,
                    )
                )
            }
        }
    }

    override suspend fun deleteHargaKavlingModel(kavlingKode: String): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            hargaKavlingDao.deleteHargaKavling(kavlingKode)
        }
    }

    override suspend fun deleteAll(): Result<Nothing?> {
        return try {
            hargaKavlingDao.deleteAll()

            roomDatabase.close()

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            roomDatabase.close()

            Result.failure(e)
        }
    }

}