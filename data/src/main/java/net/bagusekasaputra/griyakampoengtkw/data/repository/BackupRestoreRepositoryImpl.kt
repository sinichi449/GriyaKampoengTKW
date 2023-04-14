package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
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
    private val backupFeeMarketingDataSource: BackupFeeMarketingDataSource,
    private val backupBiayaLainDataSource: BackupBiayaLainDataSource,
    private val backupImageDataDiriDataSource: BackupImageDataDiriDataSource,
    private val backupFotoPembayaranDataSource: BackupFotoPembayaranDataSource,
    private val backupImageSPRDataSource: BackupImageSPRDataSource,
    private val backupRestoreDataSource: BackupRestoreDataSource,
): BackupRestoreRepository {

    override fun createBackup(backupRestoreEntity: BackupRestoreEntity): Flow<Result<Nothing?>> {
        return callbackFlow {
            try {
                val backupPath = File(internalFiles, "data_lama/${backupRestoreEntity.backupName}")
                if (backupPath.exists().not()) backupPath.mkdirs()

                // Mapping from Domain's Entity to Data Model
                val listBlok = backupRestoreEntity.listBlok.map {
                    MyObjectMapper.mapBlockModel(it)
                }
                val listKavling = backupRestoreEntity.listKavling.run {
                    val mapped = HashMap<String, List<KavlingModel>>()

                    this.keys.forEach { blok ->
                        this[blok]?.let {
                            mapped[blok] = it.map { kavling -> MyObjectMapper.mapKavling(kavling) }
                        }
                    }

                    mapped
                }
                val listPembayaran = backupRestoreEntity.listPembayaran.run {
                    val newMap = mutableMapOf<String, List<PembayaranModel>?>()

                    this.keys.forEach { kavling ->
                        newMap[kavling] = this[kavling]?.map {
                            MyObjectMapper.mapPembayaran(it)
                        }
                    }

                    newMap.toMap()
                }
                val listDataDiri = backupRestoreEntity.listDataDiri.run {
                    val newMap = HashMap<String, DataDiriModel?>()

                    this.keys.forEach { kavling ->
                        val model = this[kavling]

                        newMap[kavling] = if (model != null)
                            MyObjectMapper.mapDataDiri(model)
                        else
                            null
                    }

                    newMap
                }
                val listHargaKavling = backupRestoreEntity.listHargaKavling.run {
                    val newList = mutableListOf<HargaKavlingModel>()

                    this.forEach {
                        val model = MyObjectMapper.mapHargaKavling(it)
                        newList.add(model)
                    }

                    newList.toList()
                }
                val listCatatanPembayaran = backupRestoreEntity.listCatatanPembayaran.run {
                    val newList = mutableListOf<CatatanPembayaranModel>()

                    this.forEach {
                        val model = MyObjectMapper.mapCatatanPembayaran(it)
                        newList.add(model)
                    }

                    newList.toList()
                }
                val listBiayaMarketing = backupRestoreEntity.listBiayaMarketing.run {
                    val newList = mutableListOf<BiayaMarketingModel>()

                    this.forEach {
                        newList.add(MyObjectMapper.mapBiayaMarketing(it))
                    }

                    newList.toList()
                }
                val listFeeMarketing = backupRestoreEntity.listFeeMarketing.run {
                    val newList = mutableListOf<FeeMarketingModel>()

                    this.forEach {
                        newList.add(MyObjectMapper.mapFeeMarketing(it))
                    }

                    newList.toList()
                }
                val listBiayaLain = backupRestoreEntity.listBiayaLain.run {
                    val newList = mutableListOf<BiayaLainModel>()

                    this.forEach {
                        newList.add(MyObjectMapper.mapBiayaLain(it))
                    }

                    newList.toList()
                }
                val listImageDataDiri = backupRestoreEntity.listImageDataDiriUri.run {
                    val newList = mutableListOf<ImageDataDiriModel>()

                    this.forEach {
                        newList.add(MyObjectMapper.mapImageDataDiri(it))
                    }

                    newList.toList()
                }
                val listFotoPembayaran = backupRestoreEntity.listFotoPembayaran.run {
                    val newList = mutableListOf<FotoPembayaranModel>()

                    this.forEach { fotoPembayaran ->
                        newList.add(MyObjectMapper.mapFotoPembayaran(fotoPembayaran))
                    }

                    newList.toList()
                }
                val listImageSpr = backupRestoreEntity.listImageSprUri.run {
                    val newList = mutableListOf<ImageSprModel>()

                    this.forEach {
                        newList.add(MyObjectMapper.mapImageSpr(it))
                    }

                    newList.toList()
                }


                // Create invidiual data backup to specific path
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
                backupFeeMarketingDataSource.createBackup(backupPath.absolutePath, listFeeMarketing).onFailure {
                    throw it
                }
                backupBiayaLainDataSource.createBackup(backupPath.absolutePath, listBiayaLain).onFailure {
                    throw it
                }
                backupImageDataDiriDataSource.createBackup(backupPath.absolutePath, listImageDataDiri).onFailure {
                    throw it
                }
                backupFotoPembayaranDataSource.createBackup(backupPath.absolutePath, listFotoPembayaran).onFailure {
                    throw it
                }
                backupImageSPRDataSource.createBackup(backupPath.absolutePath, listImageSpr).onFailure { throw it }


                // Save all backup to requested SavePath
                backupRestoreDataSource.createZippedBackup(
                    backupPath = backupPath.absolutePath,
                    backupName = backupRestoreEntity.backupName,
                    savePath = backupRestoreEntity.backupSavePath,
                )
                    .onSuccess {
                        trySendBlocking(Result.success(null))
                    }
                    .onFailure {
                        it.printStackTrace()

                        throw it
                    }
            } catch (e: Exception) {
                e.printStackTrace()

                trySendBlocking(Result.failure(e))
            }

            awaitClose {  }
        }
    }
}