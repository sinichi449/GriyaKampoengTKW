package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BackupRestoreEntity
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.*

class CreateBackupAsyncUseCase(
    private val blokRepository: BlockRepository,
    private val kavlingRepository: KavlingRepository,
    private val pembayaranRepository: PembayaranRepository,
    private val dataDiriRepository: DataDiriRepository,
    private val hargaKavlingRepository: HargaKavlingRepository,
    private val catatanPembayaranRepository: CatatanPembayaranRepository,
    private val biayaMarketingRepository: BiayaMarketingRepository,
    private val backupRestoreRepository: BackupRestoreRepository,
): AsyncUseCase<CreateBackupAsyncUseCase.Request, CreateBackupAsyncUseCase.Progress>() {

    data class Progress(
        val progress: Int,
        val message: String,
    )
    data class Request(val backupName: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Progress?>> {
        return callbackFlow {
            trySendBlocking(Result.success(Progress(7, "Mendownload Blok ...")))
            val listBlok = blokRepository.getAllBlocks(dataMode = DataMode.ONLINE)
                .first().getOrThrow() ?: emptyList()

            trySendBlocking(Result.success(Progress(14, "Mendownload Kavling ...")))
            val blokKodes = mutableListOf<String>().apply {
                listBlok.forEach {
                    this.add(it.kode)
                }
            }
            val listKavlings = kavlingRepository.getAllKavlings(blokKodes)
                .first().getOrThrow() ?: HashMap()

            // List of String containing all kavling kode
            // Useful for passing arguments to *.getBatch()
            val listKodeKavlings = mutableListOf<String>().apply {
                listKavlings.keys.forEach { blok ->
                    listKavlings[blok]?.forEach { kavling ->
                        this.add(kavling.kode)
                    }
                }
            }

            trySendBlocking(Result.success(Progress(21, "Mendownload Pembayaran ...")))
            val listPembayaran = pembayaranRepository.getBatch(listKavling = listKodeKavlings)
                .first().getOrThrow() ?: HashMap()

            trySendBlocking(Result.success(Progress(28, "Mendownload Data Diri")))
            val listDataDiri = dataDiriRepository.getBatch(listKavling = listKodeKavlings)
                .first().getOrThrow() ?: HashMap()

            trySendBlocking(Result.success(Progress(35, "Mendownload Harga Kavling")))
            val listHargaKavling = (hargaKavlingRepository.getBatch(listKavling = listKodeKavlings)
                .first().getOrThrow() ?: HashMap()).run {
                // Convert this into List<HargaKavling> first
                val newList = mutableListOf<HargaKavling>()

                this.keys.forEach { kavling ->
                    val hargaKavling = this[kavling]
                    if (hargaKavling != null) {
                        newList.add(hargaKavling)
                    }
                }

                newList
            }

            trySendBlocking(Result.success(Progress(42, "Mendownload Catatan Pembayaran")))
            val listCatatanPembayaran = catatanPembayaranRepository.getBatch(listKavling = listKodeKavlings)
                .first().getOrThrow() ?: emptyList()

            trySendBlocking(Result.success(Progress(49, "Mendownload Biaya Marketing")))
            val listBiayaMarketing = (biayaMarketingRepository.getBatchOnline(listKavling = listKodeKavlings)
                .first().getOrThrow() ?: HashMap()).run {
                val newList = mutableListOf<BiayaMarketing>()

                this.keys.forEach {  kavling ->
                    this[kavling]?.forEach { biayaMarketing ->
                        newList.add(biayaMarketing)
                    }
                }

                newList.toList()
            }


            val backupRestoreEntity = BackupRestoreEntity(
                request.backupName,
                listBlok,
                listKavlings,
                listPembayaran,
                listDataDiri,
                listHargaKavling,
                listCatatanPembayaran,
                listBiayaMarketing,
            )

            backupRestoreRepository.createBackup(backupRestoreEntity)
                .first()
                .onSuccess {
                    trySendBlocking(Result.success(Progress(100, "Completed")))
                }
                .onFailure {
                    trySendBlocking(Result.failure(it))
                }

            awaitClose {  }
        }
    }
}