package net.bagusekasaputra.griyakampoeng.tkw.data.local.biayaMarketing

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalBiayaMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaMarketingModel

class RoomBiayaMarketingDataSource(
    roomDatabase: MyRoomDatabase
): LocalBiayaMarketingDataSource {

    private val biayaMarketingDao = roomDatabase.getBiayaMarketingV2Dao()

    override suspend fun getAllBiayaMarketing(kavlingKode: String): Result<Map<Long, BiayaMarketingModel>?> {
        return RoomRequestHelper.doGetOperation {
            val biayaMarketingWithId = mutableMapOf<Long, BiayaMarketingModel>()

            biayaMarketingDao.getAll(kavlingKode)?.map {
                biayaMarketingWithId[it.id ?: 0L] = mapBiayaMarketing(it)
            }

            biayaMarketingWithId
        }
    }

    override suspend fun addBiayaMarketing(
        kavlingKode: String,
        biayaMarketingModel: BiayaMarketingModel
    ): Result<Nothing?> {
        val targetData = biayaMarketingModel.let {
            biayaMarketingDao.getBiayaMarketing(
                kavlingKode = it.kavlingKode,
                tanggal = it.tanggal,
                jenisBiaya = it.jenisBiaya,
                harga = it.harga,
            )
        }
        return RoomRequestHelper.doInsertPreventDuplicateOperation(
            outerData = biayaMarketingModel,
            targetData = targetData,
            equalityPredicate = { m, e ->
                ((m.kavlingKode == e.kavlingKode)
                        and (m.tanggal == e.tanggal)
                        and (m.jenisBiaya == e.jenisBiaya)
                        and (m.harga == e.harga))
            },
            insertWork = {
                biayaMarketingDao.insertBiayaMarketing(mapBiayaMarketing(biayaMarketingModel))
            }
        )
    }

    override suspend fun update(
        id: Long,
        newBiayaMarketingModel: BiayaMarketingModel
    ): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            newBiayaMarketingModel.let {
                biayaMarketingDao.updateBiayaMarketing(
                    id = id,
                    newTanggal = it.tanggal,
                    kavlingKode = it.kavlingKode,
                    newJenisBiaya = it.jenisBiaya,
                    newHarga = it.harga,
                )
            }
        }
    }

    override suspend fun deleteSingle(id: Long): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            biayaMarketingDao.deleteById(id)
        }
    }

    override suspend fun deleteAllBiayaMarketing(kavlingKode: String): Result<Nothing?> {
        return RoomRequestHelper.doNonGetOperation {
            biayaMarketingDao.deleteAll(kavlingKode)
        }
    }

    private fun mapBiayaMarketing(biayaMarketingV2RoomEntity: BiayaMarketingV2RoomEntity): BiayaMarketingModel {
        return biayaMarketingV2RoomEntity.let {
            BiayaMarketingModel(
                kavlingKode = it.kavlingKode,
                tanggal = it.tanggal,
                jenisBiaya = it.jenisBiaya,
                harga = it.harga,
            )
        }
    }

    private fun mapBiayaMarketing(biayaMarketingModel: BiayaMarketingModel): BiayaMarketingV2RoomEntity {
        return biayaMarketingModel.let {
            BiayaMarketingV2RoomEntity(
                kavlingKode = it.kavlingKode,
                tanggal = it.tanggal,
                jenisBiaya = it.jenisBiaya,
                harga = it.harga,
            )
        }
    }
}