package net.bagusekasaputra.griyakampoeng.tkw.data.local.sources

import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.TambahanPembayaranEntity
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalTambahanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.TambahanPembayaranModel

class RoomTambahanPembayaranDataSource(
    myRoomDatabase: MyRoomDatabase
): LocalTambahanPembayaranDataSource {

    private val dao = myRoomDatabase.getTambahanPembayaranDao()

    override suspend fun getAll(kavling: String): Result<List<TambahanPembayaranModel>?> {
        return try {
            val result = dao.getAll(kavling)

            Result.success(result?.map { mapTambahanPembayaran(it) })
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun getById(kavling: String, id: String): Result<TambahanPembayaranModel?> {
        return try {
            val result = dao.getById(kavling, id)

            if (result != null) {
                Result.success(mapTambahanPembayaran(result))
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun insert(model: TambahanPembayaranModel): Result<Nothing?> {
        return try {
            dao.insert(mapTambahanPembayaran(model))

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun insertAll(models: List<TambahanPembayaranModel>): Result<Nothing?> {
        return try {
            models.forEach {
                dao.insert(mapTambahanPembayaran(it))
            }

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun update(
        kavling: String,
        id: String,
        newData: TambahanPembayaranModel
    ): Result<Nothing?> {
        return try {
            val oldData = dao.getById(kavling, id)
            if (oldData == null) {
                Result.failure(Exception("Cache update gagal: Data tidak ditemukan"))
            } else {
                dao.delete(kavling, id)

                val newEntity = mapTambahanPembayaran(newData)
                dao.insert(newEntity)

                Result.success(null)
            }
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun delete(kavling: String, id: String): Result<Nothing?> {
        return try {
            dao.delete(kavling, id)

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun deleteAll(): Result<Nothing?> {
        return try {
            dao.deleteAll()

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    private fun mapTambahanPembayaran(entity: TambahanPembayaranEntity): TambahanPembayaranModel {
        return entity.let {
            TambahanPembayaranModel(
                id = it.pembayaranId,
                kavling = it.kavling,
                kategori = it.kategori,
                tanggal = it.tanggal,
                jumlahUang = it.jumlahUang,
                keterangan = it.keterangan,
                timeMillis = it.timeMillis,
            )
        }
    }

    private fun mapTambahanPembayaran(model: TambahanPembayaranModel): TambahanPembayaranEntity {
        return model.let {
            TambahanPembayaranEntity(
                pembayaranId = it.id,
                kavling = it.kavling,
                kategori = it.kategori,
                tanggal = it.tanggal,
                jumlahUang = it.jumlahUang,
                keterangan = it.keterangan,
                timeMillis = it.timeMillis,
            )
        }
    }
}