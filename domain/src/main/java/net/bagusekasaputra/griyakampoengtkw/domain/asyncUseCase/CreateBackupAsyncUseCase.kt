package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BackupRestoreEntity
import net.bagusekasaputra.griyakampoengtkw.domain.repository.*

class CreateBackupAsyncUseCase(
    private val blokRepository: BlockRepository,
    private val kavlingRepository: KavlingRepository,
    private val pembayaranRepository: PembayaranRepository,
    private val dataDiriRepository: DataDiriRepository,
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



            val backupRestoreEntity = BackupRestoreEntity(
                backupName = request.backupName,
                listBlok = listBlok,
                listKavling = listKavlings,
                listPembayaran = listPembayaran,
                listDataDiri = listDataDiri,
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