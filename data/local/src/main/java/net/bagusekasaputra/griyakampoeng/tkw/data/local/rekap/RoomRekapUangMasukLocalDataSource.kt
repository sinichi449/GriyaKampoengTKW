package net.bagusekasaputra.griyakampoeng.tkw.data.local.rekap

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalRekapUangMasukDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.RekapUangMasukModel

class RoomRekapUangMasukLocalDataSource(
    private val roomDatabase: MyRoomDatabase
): LocalRekapUangMasukDataSource {

    private val rekapDao = roomDatabase.getRekapUangMasukDao()

    override suspend fun getAll(): Result<List<RekapUangMasukModel>?> {
        return try {
            val listEntity = rekapDao.getAll()?.map {
                RekapUangMasukModel(
                    noKavling = it.noKavling,
                    namaCostumer = it.namaCostumer,
                    tanggal = it.tanggal,
                    jenisPembayaran = it.jenisPembayaran,
                    jumlahPembayaran = it.jumlahPembayaran,
                )
            }

            Result.success(listEntity)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun insert(model: RekapUangMasukModel): Result<Nothing?> {
        return try {
            rekapDao.insert(
                RekapUangMasukEntity(
                    noKavling = model.noKavling,
                    namaCostumer = model.namaCostumer,
                    tanggal = model.tanggal,
                    jenisPembayaran = model.jenisPembayaran,
                    jumlahPembayaran = model.jumlahPembayaran,
                )
            )

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun clearAll(): Result<Nothing?> {
        return try {
            rekapDao.clearAll()

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

}