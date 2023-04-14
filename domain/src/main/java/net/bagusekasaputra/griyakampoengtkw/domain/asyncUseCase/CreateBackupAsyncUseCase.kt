package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BackupRestoreEntity
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing
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
    private val feeMarketingRepository: FeeMarketingRepository,
    private val biayaLainRepository: BiayaLainRepository,
    private val imageDataDiriRepository: ImageDataDiriRepository,
    private val fotoPembayaranRepository: FotoPembayaranRepository,
    private val imageSprRepository: ImageSprRepository,
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

            trySendBlocking(Result.success(Progress(56, "Mendownload Fee Marketing")))
            val listFeeMarketing = (feeMarketingRepository.getBatchOnline(listKavling = listKodeKavlings)
                .first().getOrThrow() ?: HashMap()).run {
                val newList = mutableListOf<FeeMarketing>()

                this.keys.forEach { kavling ->
                    val feeMarketing = this[kavling]

                    if (feeMarketing != null) newList.add(feeMarketing)
                }

                newList.toList()
            }

            trySendBlocking(Result.success(Progress(63, "Mendownload Biaya Lain-lain")))
            val listBiayaLain = biayaLainRepository.getAllOnline(dataMode = DataMode.ONLINE)
                .first().getOrThrow() ?: emptyList()

            trySendBlocking(Result.success(Progress(70, "Mendownload Foto Data Diri")))
            val listImageDataDiriUri = imageDataDiriRepository.getBatchUri(listKavling = listKodeKavlings)
                .first().getOrNull() ?: emptyList()

            trySendBlocking(Result.success(Progress(77, "Mendownload Foto Pembayaran")))
            val mapKavlingTermin = mutableMapOf<String, List<String>>().run {
                listPembayaran.keys.forEach { kavling ->
                    val listTermin = mutableListOf<String>()

                    listPembayaran[kavling]?.forEach { pembayaran ->
                        listTermin.add(pembayaran.termin)
                    }

                    this[kavling] = listTermin.toList()
                }

                this
            }
            val listFotoPembayaran = fotoPembayaranRepository.getBatchUri(mapKavlingTermin)
                .first().getOrThrow() ?: emptyList()

            trySendBlocking(Result.success(Progress(84, "Mendownload Foto SPR")))
            val listImageSprUri = imageSprRepository.getBatchUri(listKavling = listKodeKavlings)
                .first().getOrThrow() ?: emptyList()


            val backupRestoreEntity = BackupRestoreEntity(
                request.backupName,
                listBlok,
                listKavlings,
                listPembayaran,
                listDataDiri,
                listHargaKavling,
                listCatatanPembayaran,
                listBiayaMarketing,
                listFeeMarketing,
                listBiayaLain,
                listImageDataDiriUri,
                listFotoPembayaran,
                listImageSprUri,
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