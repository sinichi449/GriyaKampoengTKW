package net.bagusekasaputra.griyakampoeng.tkw.data.local.biayaLain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalBiayaLainDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaLainModel

class RoomBiayaLainDataSource(
    private val roomDatabase: MyRoomDatabase
): LocalBiayaLainDataSource {

    private val dao = roomDatabase.getBiayaLainDao()

    override fun getAll(): Flow<Result<List<BiayaLainModel>?>> {
        return flow {
            val listModel = dao.getAll()

            roomDatabase.close()

            emit(Result.success(
                listModel?.map {
                    BiayaLainModel(
                        jenisBiaya = it.jenisBiaya,
                        harga = it.harga,
                        tanggal = it.tanggal
                    )
                }
            ))
        }
    }

    override fun getSingle(jenisBiaya: String): Flow<Result<BiayaLainModel?>> {
        return flow {
            val model = dao.getSingle(jenisBiaya)

            roomDatabase.close()

            emit(Result.success(
                model?.let {
                    BiayaLainModel(
                        jenisBiaya = it.jenisBiaya,
                        harga = it.harga,
                        tanggal = it.tanggal
                    )
                }
            ))
        }
    }

    override suspend fun insertAll(listModel: List<BiayaLainModel>): Result<Nothing?> {
        return try {
            listModel.forEach { model ->
                dao.insert(
                    BiayaLainRoomEntity(
                        jenisBiaya = model.jenisBiaya,
                        harga = model.harga,
                        tanggal = model.tanggal,
                    )
                )
            }

            roomDatabase.close()

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            roomDatabase.close()

            Result.failure(e)
        }
    }

    override suspend fun insert(model: BiayaLainModel): Result<Nothing?> {
        return try {
            dao.insert(
                BiayaLainRoomEntity(
                    jenisBiaya = model.jenisBiaya,
                    harga = model.harga,
                    tanggal = model.tanggal,
                )
            )

            roomDatabase.close()

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            roomDatabase.close()

            Result.failure(e)
        }
    }

    override suspend fun update(
        oldModel: BiayaLainModel,
        newModel: BiayaLainModel,
    ): Result<Nothing?> {
        return try {
            dao.update(
                jenisBiaya = oldModel.jenisBiaya,
                newJenisBiaya = newModel.jenisBiaya,
                newHarga = newModel.harga,
                newTanggal = newModel.tanggal,
            )

            roomDatabase.close()

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            roomDatabase.close()

            Result.failure(e)
        }
    }

    override suspend fun delete(model: BiayaLainModel): Result<Nothing?> {
        return try {
            dao.delete(model.jenisBiaya)

            roomDatabase.close()

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            roomDatabase.close()

            Result.failure(e)
        }
    }

    override suspend fun deleteAll(): Result<Nothing?> {
        return try {
            dao.deleteAll()

            roomDatabase.close()

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            roomDatabase.close()

            Result.failure(e)
        }
    }
}