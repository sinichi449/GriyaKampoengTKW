package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.*
import net.bagusekasaputra.griyakampoengtkw.data.model.*
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BackupRestoreEntity
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BackupRestoreRepository
import java.io.File

class BackupRestoreRepositoryImpl(
    private val internalFiles: File,
    private val backupBlokDataSource: BackupBlokDataSource,
    private val backupKavlingDataSource: BackupKavlingDataSource,
    private val backupPembayaranDataSource: BackupPembayaranDataSource,
    private val backupDataDiriDataSource: BackupDataDiriDataSource,
    private val backupHargaKavlingDataSource: BackupHargaKavlingDataSource,
    private val backupCatatanPembayaranDataSource: BackupCatatanPembayaranDataSource,
    private val backupBiayaMarketingDataSource: BackupBiayaMarketingDataSource,
): BackupRestoreRepository {

    override fun createBackup(backupRestoreEntity: BackupRestoreEntity): Flow<Result<Nothing?>> {
        return callbackFlow {
            try {
                val backupPath = File(internalFiles, "data_lama/${backupRestoreEntity.backupName}")
                if (backupPath.exists().not()) backupPath.mkdirs()

                // Mapping from Domain's Entity to Data Model
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
                val listHargaKavling = backupRestoreEntity.listHargaKavling.run {
                    val newList = mutableListOf<HargaKavlingModel>()

                    this.forEach {
                        val model = HargaKavlingRepositoryImpl.mapHargaKavling(it)
                        newList.add(model)
                    }

                    newList.toList()
                }
                val listCatatanPembayaran = backupRestoreEntity.listCatatanPembayaran.run {
                    val newList = mutableListOf<CatatanPembayaranModel>()

                    this.forEach {
                        val model = CatatanPembayaranRepositoryImpl.mapCatatanPembayaran(it)
                        newList.add(model)
                    }

                    newList.toList()
                }
                val listBiayaMarketing = backupRestoreEntity.listBiayaMarketing.run {
                    val newList = mutableListOf<BiayaMarketingModel>()

                    this.forEach {
                        newList.add(BiayaMarketingRepositoryImpl.mapBiayaMarketing(it))
                    }

                    newList.toList()
                }


                backupBlokDataSource.createBackup(backupPath.absolutePath, listBlok).onFailure {
                    throw it
                }
                backupKavlingDataSource.createBackup(backupPath.absolutePath, listKavling).onFailure {
                    throw it
                }
                backupPembayaranDataSource.createBackup(backupPath.absolutePath, listPembayaran).onFailure {
                    throw it
                }
                backupDataDiriDataSource.createBackup(backupPath.absolutePath, listDataDiri).onFailure {
                    throw it
                }
                backupHargaKavlingDataSource.createBackup(backupPath.absolutePath, listHargaKavling).onFailure {
                    throw it
                }
                backupCatatanPembayaranDataSource.createBackup(backupPath.absolutePath, listCatatanPembayaran).onFailure {
                    throw it
                }
                backupBiayaMarketingDataSource.createBackup(backupPath.absolutePath, listBiayaMarketing).onFailure {
                    throw it
                }


                trySendBlocking(Result.success(null))
            } catch (e: Exception) {
                trySendBlocking(Result.failure(e))
            }

            awaitClose {  }
        }
    }
}