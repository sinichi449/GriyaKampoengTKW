package net.bagusekasaputra.griyakampoeng.tkw.data.local.sources

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.BiayaLainRoomEntity
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalBiayaLainDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaLainModel

class RoomBiayaLainDataSource(
    private val roomDatabase: MyRoomDatabase
): LocalBiayaLainDataSource {

    private val dao = roomDatabase.getBiayaLainDao()

    override fun getAll(): Flow<Result<List<BiayaLainModel>?>> {
        return flow {
            val listModel = dao.getAll()

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
                // Refactor <-> Duplication
                val result = insert(model)
                if (result.isFailure) {
                    throw Exception()
                }
            }

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun insert(model: BiayaLainModel): Result<Nothing?> {
        return try {
            // only write to non-null data
            if (model.jenisBiaya.isNotEmpty())
                dao.insert(
                    BiayaLainRoomEntity(
                        jenisBiaya = model.jenisBiaya,
                        harga = model.harga,
                        tanggal = model.tanggal,
                    )
                )



            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()



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



            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()



            Result.failure(e)
        }
    }

    override suspend fun delete(model: BiayaLainModel): Result<Nothing?> {
        return try {
            dao.delete(model.jenisBiaya)



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
}