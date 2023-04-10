package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupBlokDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel
import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingModel
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BackupRestoreEntity
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BackupRestoreRepository
import java.io.File

class BackupRestoreRepositoryImpl(
    private val internalFiles: File,
    private val backupBlokDataSource: BackupBlokDataSource,
    private val backupKavlingDataSource: BackupKavlingDataSource,
    private val backupPembayaranDataSource: BackupPembayaranDataSource,
    private val backupDataDiriDataSource: BackupDataDiriDataSource,
): BackupRestoreRepository {

    override fun createBackup(backupRestoreEntity: BackupRestoreEntity): Flow<Result<Nothing?>> {
        return callbackFlow {
            try {
                val backupPath = File(internalFiles, "data_lama/${backupRestoreEntity.backupName}")
                if (backupPath.exists().not()) backupPath.mkdirs()

                // Mapping
                val listBlok = backupRestoreEntity.listBlok.map {
                    BlockRepositoryImpl.mapBlockModel(it)
                }
                val listKavling = backupRestoreEntity.listKavling.run {
                    val mapped = HashMap<String, List<KavlingModel>>()

                    this.keys.forEach { blok ->
                        this[blok]?.let {
                            mapped[blok] = it.map { kavling -> KavlingRepositoryImpl.mapKavling(kavling) }
                        }
                    }

                    mapped
                }
                val listPembayaran = backupRestoreEntity.listPembayaran.run {
                    val newMap = mutableMapOf<String, List<PembayaranModel>?>()

                    this.keys.forEach { kavling ->
                        newMap[kavling] = this[kavling]?.map {
                            PembayaranRepositoryImpl.mapPembayaran(it)
                        }
                    }

                    newMap.toMap()
                }
                val listDataDiri = backupRestoreEntity.listDataDiri.run {
                    val newMap = HashMap<String, DataDiriModel?>()

                    this.keys.forEach { kavling ->
                        val model = this[kavling]

                        newMap[kavling] = if (model != null)
                            DataDiriRepositoryImpl.mapDataDiri(model)
                        else
                            null
                    }

                    newMap
                }


                backupBlokDataSource.createBackup(backupPath.absolutePath, listBlok).onFailure {
                    trySendBlocking(Result.failure(it))
                }
                backupKavlingDataSource.createBackup(backupPath.absolutePath, listKavling).onFailure {
                    trySendBlocking(Result.failure(it))
                }
                backupPembayaranDataSource.createBackup(backupPath.absolutePath, listPembayaran).onFailure {
                    trySendBlocking(Result.failure(it))
                }
                backupDataDiriDataSource.createBackup(backupPath.absolutePath, listDataDiri).onFailure {
                    trySendBlocking(Result.failure(it))
                }

                trySendBlocking(Result.success(null))
            } catch (e: Exception) {
                trySendBlocking(Result.failure(e))
            }

            awaitClose {  }
        }
    }
}