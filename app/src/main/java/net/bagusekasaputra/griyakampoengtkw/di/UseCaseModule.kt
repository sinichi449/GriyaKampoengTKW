package net.bagusekasaputra.griyakampoengtkw.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.CreateBackupAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaLain.*
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaMarketing.GetAllBiayaMarketingByKavlingKodeAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaMarketing.GetRekapBiayaMarketingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.block.GetAllBlocksAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.catatanPembayaran.GetCatatanPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.dataDiri.GetDataDiriAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.feeMarketing.GetFeeMarketingByKavlingKodeAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.feeMarketing.GetRekapFeeMarketingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoPembayaran.AddFotoPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoPembayaran.DeleteFotoPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoPembayaran.GetFotoPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoPembayaran.IsFotoPembayaranExistAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.hargaKavling.GetHargaKavlingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetKavlingByBlockAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran.GetAllPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pengingat.*
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetListRekapGlobalAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetRekapBesarAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetUangMasukRekapAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.reportKavling.GetAllReportKavlingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.repository.*
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.appupdate.GetUpdateInformationUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.biayaMarketing.*
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.block.AddNewBlockUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.catatanPembayaran.AddCatatanPembayaranUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.catatanPembayaran.DeleteCatatanPembayaranUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.datadiri.AddDataDiriUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.datadiri.DeleteDataDiriUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.feeMarketing.AddFeeMarketingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.feeMarketing.DeleteFeeMarketingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.feeMarketing.UpdateFeeMarketingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.fotoKuitansi.AddFotoKuitansiUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.fotoKuitansi.GetFotoKuitansiUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.hargakavling.AddHargaKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.hargakavling.GetSingleHargaKavlingForPembayaranUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageDataDiri.AddImageDataDiriUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageDataDiri.DeleteImageDataDiriUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageDataDiri.GetImageDataDiriByKavlingKodeUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageSpr.AddImageSprUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageSpr.GetImageSprByKavlingKodeUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling.AddKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling.EditKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.kavling.RemoveKavlingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.pembayaran.*
import java.io.File

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    /**
     * App Update
     */
    @Provides
    fun provideGetAppUpdateInformation(appUpdateRepository: AppUpdateRepository)
        = GetUpdateInformationUseCase(appUpdateRepository)


    /**
     * Block
     */
    @Provides
    fun provideGetAllBlocks(blockRepository: BlockRepository)
        = GetAllBlocksAsyncUseCase(blockRepository)

    @Provides
    fun provideAddNewBlock(blockRepository: BlockRepository)
        = AddNewBlockUseCase(blockRepository)


    /**
     * Kavling
     */
    @Provides
    fun provideGetKavlingsByBlockAsync(kavlingRepository: KavlingRepository, pembayaranRepository: PembayaranRepository)
        = GetKavlingByBlockAsyncUseCase(kavlingRepository, pembayaranRepository)

    @Provides
    fun provideAddKavlingUseCase(kavlingRepository: KavlingRepository)
        = AddKavlingUseCase(kavlingRepository)

    @Provides
    fun provideEditKavling(kavlingRepository: KavlingRepository)
        = EditKavlingUseCase(kavlingRepository)

    @Provides
    fun provideRemoveKavling(kavlingRepository: KavlingRepository)
        = RemoveKavlingUseCase(kavlingRepository)


    /**
     * Data Diri
     */
    @Provides
    fun provideGetDataDiri(dataDiriRepository: DataDiriRepository)
        = GetDataDiriAsyncUseCase(dataDiriRepository)

    @Provides
    fun provideAddDataDiri(dataDiriRepository: DataDiriRepository): AddDataDiriUseCase {
        return AddDataDiriUseCase(dataDiriRepository)
    }

    @Provides
    fun provideDeleteDataDiri(dataDiriRepository: DataDiriRepository): DeleteDataDiriUseCase {
        return DeleteDataDiriUseCase(dataDiriRepository)
    }


    /**
     * Harga Kavling
     */
    @Provides
    fun provideGetHargaKavling(hargaKavlingRepository: HargaKavlingRepository): GetHargaKavlingAsyncUseCase {
        return GetHargaKavlingAsyncUseCase(hargaKavlingRepository)
    }

    @Provides
    fun provideAddHargaKavling(hargaKavlingRepository: HargaKavlingRepository): AddHargaKavlingUseCase {
        return AddHargaKavlingUseCase(hargaKavlingRepository)
    }

    @Provides
    fun provideGetSingleHargaKavling(hargaKavlingRepository: HargaKavlingRepository): GetSingleHargaKavlingForPembayaranUseCase {
        return GetSingleHargaKavlingForPembayaranUseCase(hargaKavlingRepository)
    }

    /**
     * Pembayaran
     */
    @Provides
    fun provideGetAllPembayaran(
        pembayaranRepository: PembayaranRepository,
        hargaKavlingRepository: HargaKavlingRepository,
        fotoPembayaranRepository: FotoPembayaranRepository,
    )
        = GetAllPembayaranAsyncUseCase(pembayaranRepository, hargaKavlingRepository, fotoPembayaranRepository)

    @Provides
    fun provideAddPembayaranUseCase(pembayaranRepository: PembayaranRepository): AddPembayaranUseCase {
        return AddPembayaranUseCase(pembayaranRepository)
    }

    @Provides
    fun provideUpdatePembayaranUseCase(pembayaranRepository: PembayaranRepository): UpdatePembayaranUseCase {
        return UpdatePembayaranUseCase(pembayaranRepository)
    }

    @Provides
    fun provideDeletePembayaranByTermin(
        pembayaranRepository: PembayaranRepository,
        fotoPembayaranRepository: FotoPembayaranRepository
    ): DeletePembayaranByTerminUseCase {
        return DeletePembayaranByTerminUseCase(pembayaranRepository, fotoPembayaranRepository)
    }

    @Provides
    fun provideDeleteAllPembayaran(pembayaranRepository: PembayaranRepository, fotoPembayaranRepository: FotoPembayaranRepository): DeleteAllPembayaranUseCase {
        return DeleteAllPembayaranUseCase(pembayaranRepository, fotoPembayaranRepository)
    }


    /**
     * Biaya Marketing
     */
    @Provides
    fun provideGetAllBiayaMarketingByKavlingKode(
        biayaMarketingRepository: BiayaMarketingRepository,
        feeMarketingRepository: FeeMarketingRepository
    )
        = GetAllBiayaMarketingByKavlingKodeAsyncUseCase(biayaMarketingRepository, feeMarketingRepository)


    @Provides
    fun provideAddBiayaMarketing(biayaMarketingRepository: BiayaMarketingRepository)
        = AddBiayaMarketingUseCase(biayaMarketingRepository)


    @Provides
    fun provideEditBiayaMarketingUseCase(biayaMarketingRepository: BiayaMarketingRepository)
        = EditBiayaMarketingUseCase(biayaMarketingRepository)


    @Provides
    fun provideDeleteSingleBiayaMarketing(biayaMarketingRepository: BiayaMarketingRepository)
        = DeleteSingleBiayaMarketingUseCase(biayaMarketingRepository)

    @Provides
    fun provideDeleteAllBiayaMarketing(biayaMarketingRepository: BiayaMarketingRepository)
        = DeleteAllBiayaMarketingUseCase(biayaMarketingRepository)


    /**
     * Fee Marketing
     */
    @Provides
    fun provideGetFeeMarketingByKavlingKode(feeMarketingRepository: FeeMarketingRepository)
        = GetFeeMarketingByKavlingKodeAsyncUseCase(feeMarketingRepository)

    @Provides
    fun provideAddFeeMarketing(feeMarketingRepository: FeeMarketingRepository)
        = AddFeeMarketingUseCase(feeMarketingRepository)

    @Provides
    fun provideUpdateFeeMarketing(feeMarketingRepository: FeeMarketingRepository)
        = UpdateFeeMarketingUseCase(feeMarketingRepository)

    @Provides
    fun provideDeleteFeeMarketing(feeMarketingRepository: FeeMarketingRepository)
        = DeleteFeeMarketingUseCase(feeMarketingRepository)


    /**
     * Catatan Pembayaran
     */
    @Provides
    fun provideGetCatatanPembayaran(catatanPembayaranRepository: CatatanPembayaranRepository)
        = GetCatatanPembayaranAsyncUseCase(catatanPembayaranRepository)

    @Provides
    fun provideAddCatatanPembayaran(catatanPembayaranRepository: CatatanPembayaranRepository)
        = AddCatatanPembayaranUseCase(catatanPembayaranRepository)

    @Provides
    fun provideDeleteCatatanPembayaran(catatanPembayaranRepository: CatatanPembayaranRepository)
        = DeleteCatatanPembayaranUseCase(catatanPembayaranRepository)

    /**
     * Image Data Diri
     */
    @Provides
    fun provideGetImageDataDiriByKavlingKode(imageDataDiriRepository: ImageDataDiriRepository)
        = GetImageDataDiriByKavlingKodeUseCase(imageDataDiriRepository)

    @Provides
    fun provideAddImageDataDiri(imageDataDiriRepository: ImageDataDiriRepository)
        = AddImageDataDiriUseCase(imageDataDiriRepository)

    @Provides
    fun provideDeleteImageDataDiri(imageDataDiriRepository: ImageDataDiriRepository)
        = DeleteImageDataDiriUseCase(imageDataDiriRepository)


    /**
     * Image SPR
     */
    @Provides
    fun provideGetImageSprByKavlingKode(imageSprRepository: ImageSprRepository)
        = GetImageSprByKavlingKodeUseCase(imageSprRepository)

    @Provides
    fun provideAddImageSpr(imageSprRepository: ImageSprRepository, @ExternalDir externalFilesDir: File?)
        = AddImageSprUseCase(imageSprRepository, externalFilesDir)


    /**
     * Foto Kuitansi
     */
    @Provides
    fun provideGetFotoKuitanse(fotoKuitansiRepository: FotoKuitansiRepository)
        = GetFotoKuitansiUseCase(fotoKuitansiRepository)

    @Provides
    fun provideAddFotoKuitansi(fotoKuitansiRepository: FotoKuitansiRepository, @ExternalDir externalFilesDir: File?)
        = AddFotoKuitansiUseCase(fotoKuitansiRepository, externalFilesDir)


    /**
     * Foto Pembayaran
     */
    @Provides
    fun provideGetFotoPembayaranAsync(fotoPembayaranRepository: FotoPembayaranRepository)
        = GetFotoPembayaranAsyncUseCase(fotoPembayaranRepository)

    @Provides
    fun provideAddFotoPembayaranAsync(fotoPembayaranRepository: FotoPembayaranRepository)
        = AddFotoPembayaranAsyncUseCase(fotoPembayaranRepository)

    @Provides
    fun provideDeleteFotoPembayaranAsync(fotoPembayaranRepository: FotoPembayaranRepository)
        = DeleteFotoPembayaranAsyncUseCase(fotoPembayaranRepository)

    @Provides
    fun provideIsFotoPembayaranExistAsync(fotoPembayaranRepository: FotoPembayaranRepository)
        = IsFotoPembayaranExistAsyncUseCase(fotoPembayaranRepository)


    /**
     * Report Kavling
     */
    @Provides
    fun provideGetAllReportKavlingUseCase(
        pembayaranRepository: PembayaranRepository,
        feeMarketingRepository: FeeMarketingRepository,
        biayaMarketingRepository: BiayaMarketingRepository,
    ): GetAllReportKavlingAsyncUseCase {
        return GetAllReportKavlingAsyncUseCase(pembayaranRepository, feeMarketingRepository, biayaMarketingRepository)
    }

    /**
     * Pengingat
     */
    @Provides
    fun provideGetAllPengingatUseCase(pengingatRepository: PengingatRepository): GetAllPengingatAsyncUseCase {
        return GetAllPengingatAsyncUseCase((pengingatRepository))
    }

    @Provides
    fun provideAddPengingatUseCase(pengingatRepository: PengingatRepository): AddPengingatAsyncUseCase {
        return AddPengingatAsyncUseCase(pengingatRepository)
    }

    @Provides
    fun provideUpdatePengingatUseCase(pengingatRepository: PengingatRepository): UpdatePengingatAsyncUseCase {
        return UpdatePengingatAsyncUseCase(pengingatRepository)
    }

    @Provides
    fun provideDeletePengingatUseCase(pengingatRepository: PengingatRepository): DeletePengingatAsyncUseCase {
        return DeletePengingatAsyncUseCase(pengingatRepository)
    }

    @Provides
    fun provideTurnOffPengingatUseCase(pengingatRepository: PengingatRepository): TurnOnOffPengingatAsyncUseCase {
        return TurnOnOffPengingatAsyncUseCase(pengingatRepository)
    }

    /**
     * Biaya Lain
     */
    @Provides
    fun provideGetAllBiayalainUseCase(biayaLainRepository: BiayaLainRepository): GetAllBiayaLainAsyncUseCase {
        return GetAllBiayaLainAsyncUseCase(biayaLainRepository)
    }

    @Provides
    fun provideAddBiayaLainUseCase(biayaLainRepository: BiayaLainRepository): AddBiayaLainAsyncUseCase {
        return AddBiayaLainAsyncUseCase(biayaLainRepository)
    }

    @Provides
    fun provideUpdateBiayaLainUseCase(biayaLainRepository: BiayaLainRepository): UpdateBiayaLainAsyncUseCase {
        return UpdateBiayaLainAsyncUseCase(biayaLainRepository)
    }

    @Provides
    fun provideDeleteBiayaLainUseCase(biayaLainRepository: BiayaLainRepository): DeleteBiayaLainAsyncUseCase {
        return DeleteBiayaLainAsyncUseCase(biayaLainRepository)
    }

    /**
     * Rekap Global
     */
    @Provides
    fun provideGetListRekapGlobalUseCase(
        dataDiriRepository: DataDiriRepository,
        pembayaranRepository: PembayaranRepository,
        hargaKavlingRepository: HargaKavlingRepository,
    ): GetListRekapGlobalAsyncUseCase {
        return GetListRekapGlobalAsyncUseCase(
            dataDiriRepository,
            pembayaranRepository,
            hargaKavlingRepository
        )
    }

    /**
     * Rekap Besar
     */
    @Provides
    fun provideGetRekapBesarUseCase(
        dataDiriRepository: DataDiriRepository,
        pembayaranRepository: PembayaranRepository,
        hargaKavlingRepository: HargaKavlingRepository,
        feeMarketingRepository: FeeMarketingRepository,
        biayaMarketingRepository: BiayaMarketingRepository,
        biayaLainRepository: BiayaLainRepository,
        rekapUangMasukRepository: RekapUangMasukRepository,
    ): GetRekapBesarAsyncUseCase {
        return GetRekapBesarAsyncUseCase(dataDiriRepository,
            pembayaranRepository,
            hargaKavlingRepository,
            feeMarketingRepository,
            biayaMarketingRepository,
            biayaLainRepository,
            rekapUangMasukRepository)
    }

    /**
     * Rekap Uang Masuk
     */
    @Provides
    fun provideGetUangMasukRekapUseCase(rekapUangMasukRepository: RekapUangMasukRepository): GetUangMasukRekapAsyncUseCase {
        return GetUangMasukRekapAsyncUseCase(rekapUangMasukRepository)
    }

    /**
     * Rekap Fee Marketing
     */
    @Provides
    fun provideGetRekapFeeMarketingUseCase(feeMarketingRepository: FeeMarketingRepository): GetRekapFeeMarketingAsyncUseCase {
        return GetRekapFeeMarketingAsyncUseCase(feeMarketingRepository)
    }

    /**
     * Rekap Biaya Marketing
     */
    @Provides
    fun provideGetRekapBiayaMarketingUseCase(biayaMarketingRepository: BiayaMarketingRepository): GetRekapBiayaMarketingAsyncUseCase {
        return GetRekapBiayaMarketingAsyncUseCase(biayaMarketingRepository)
    }

    /**
     * Rekap Biaya Lain-lain
     */
    @Provides
    fun provideGetRekapBiayaLainUseCase(biayaLainRepository: BiayaLainRepository): GetRekapBiayaLainAsyncUseCase {
        return GetRekapBiayaLainAsyncUseCase(biayaLainRepository)
    }


    /**
     * Backup / Restore
     */
    @Provides
    fun provideCreateBackupUseCase(
        blokRepository: BlockRepository,
        kavlingRepository: KavlingRepository,
        pembayaranRepository: PembayaranRepository,
        dataDiriRepository: DataDiriRepository,
        hargaKavlingRepository: HargaKavlingRepository,
        catatanPembayaranRepository: CatatanPembayaranRepository,
        biayaMarketingRepository: BiayaMarketingRepository,
        feeMarketingRepository: FeeMarketingRepository,
        biayaLainRepository: BiayaLainRepository,
        imageDataDiriRepository: ImageDataDiriRepository,
        fotoPembayaranRepository: FotoPembayaranRepository,
        backupRestoreRepository: BackupRestoreRepository,
    ): CreateBackupAsyncUseCase {
        return CreateBackupAsyncUseCase(
            blokRepository,
            kavlingRepository,
            pembayaranRepository,
            dataDiriRepository,
            hargaKavlingRepository,
            catatanPembayaranRepository,
            biayaMarketingRepository,
            feeMarketingRepository,
            biayaLainRepository,
            imageDataDiriRepository,
            fotoPembayaranRepository,
            backupRestoreRepository,
        )
    }
}