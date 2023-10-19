package net.bagusekasaputra.griyakampoeng.tkw.data.local.sources

import androidx.core.net.toFile
import androidx.core.net.toUri
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.model.FotoTambahanPembayaranEntity
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalFotoTambahanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoTambahanPembayaranModel
import java.io.File

class RoomFotoTambahanPembayaranDataSource(
    roomDatabase: MyRoomDatabase,
    private val externalFileDir: File?,
): LocalFotoTambahanPembayaranDataSource {

    private val dao = roomDatabase.getFotoTambahanPembayaranDao()

    override suspend fun get(kavling: String, id: String): Result<FotoTambahanPembayaranModel?> {
        return try {
            val entity = dao.get(kavling, id)?.toModel()

            Result.success(entity)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun insert(
        model: FotoTambahanPembayaranModel,
        fromRemote: Boolean
    ): Result<Nothing?> {
        return try {
            val entity = if (fromRemote) {
                model.toEntity()
            } else {
                // First copy from source folder
                val srcFile = model.uri.toUri().toFile()
                val dstUri = File(externalFileDir, "${FotoTambahanPembayaranModel.DST_FOLDER}/${model.getKavlingAndFilePath()}").let {
                    srcFile.renameTo(it)
                    it.toUri().toString()
                }

                model.toEntity().copy(uri = dstUri)
            }

            dao.insert(entity)

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun delete(kavling: String, id: String): Result<Nothing?> {
        return try {
            FotoTambahanPembayaranModel(
                kavling = kavling,
                tambahanPembayaranId = id,
            ).also {
                File(externalFileDir, "${FotoPembayaranModel.DST_FOLDER}/${it.getKavlingAndFilePath()}")
                    .delete()
            }

            dao.delete(kavling, id)

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun deleteAll(kavling: String): Result<Nothing?> {
        return try {
            dao.deleteByKavling(kavling)

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    private fun FotoTambahanPembayaranEntity.toModel(): FotoTambahanPembayaranModel {
        return this.let {
            FotoTambahanPembayaranModel(
                kavling = it.kavling,
                tambahanPembayaranId = it.pembayaranId,
                uri = it.uri,
            )
        }
    }

    private fun FotoTambahanPembayaranModel.toEntity(): FotoTambahanPembayaranEntity {
        return this.let {
            FotoTambahanPembayaranEntity(
                kavling = it.kavling,
                pembayaranId = it.tambahanPembayaranId,
                uri = it.uri,
            )
        }
    }
}