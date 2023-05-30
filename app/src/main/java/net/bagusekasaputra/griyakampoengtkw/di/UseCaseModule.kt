package net.bagusekasaputra.griyakampoengtkw.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.ambilKuitansi.InsertAmbilKuitansiAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.backupRestore.CreateBackupAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.backupRestore.GetListBackupAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.baselinePembayaran.GetBaselinePembayaranByKavlingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.baselinePembayaran.SetBaselinePembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaLain.*
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaMarketing.GetAllBiayaMarketingByKavlingKodeAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaPribadi.GetAllBiayaPribadiAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.block.GetAllBlocksAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.catatanPembayaran.GetCatatanPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.dataDiri.GetDataDiriAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.databaseUser.GetAllDatabaseUserAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.databaseUser.InsertDatabaseUserAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.feeMarketing.GetFeeMarketingByKavlingKodeAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoPembayaran.AddFotoPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoPembayaran.DeleteFotoPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoPembayaran.GetFotoPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.fotoPembayaran.IsFotoPembayaranExistAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.hargaKavling.GetHargaKavlingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.GetAllIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.GetAllPembayaranIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.imageDataDiri.GetFotoIdentitasIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.GetHargaRumahIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.dataDiri.EditDataDiriIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.dataDiri.GetDataDiriIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.dataDiri.InsertDataDiriIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetKavlingByBlockAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetListUnmigratedKavlingsAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetProgressKavlingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran.DeletePembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran.GetListPembayaranBulananAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pengingat.*
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.promotion.GetPromotionMessageAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.CalculateRekapBesarAndGetRekapBesarOverview
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetListRekapGlobalAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetRekapBesarDetailAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.reportKavling.GetAllReportKavlingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.statusPembayaran.GetStatusPembayaranKavlingAsyncUseCase
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

    @Provides
    fun provideGetListUnmigratedKavlingAsyncUseCase(
        kavlingRepository: KavlingRepository,
        dataDiriRepository: DataDiriRepository
    ): GetListUnmigratedKavlingsAsyncUseCase {
        return GetListUnmigratedKavlingsAsyncUseCase(kavlingRepository, dataDiriRepository)
    }


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
    fun provideAddPembayaranUseCase(pembayaranRepository: PembayaranRepository): AddPembayaranUseCase {
        return AddPembayaranUseCase(pembayaranRepository)
    }

    @Provides
    fun provideUpdatePembayaranUseCase(pembayaranRepository: PembayaranRepository): UpdatePembayaranUseCase {
        return UpdatePembayaranUseCase(pembayaranRepository)
    }

    @Provides
    fun provideDeletePembayaranUseCase(
        pembayaranRepository: PembayaranRepository,
        fotoPembayaranRepository: FotoPembayaranRepository,
        ambilKuitansiRepository: AmbilKuitansiRepository,
    ): DeletePembayaranAsyncUseCase {
        return DeletePembayaranAsyncUseCase(pembayaranRepository, fotoPembayaranRepository, ambilKuitansiRepository)
    }

    @Provides
    fun provideDeleteAllPembayaran(pembayaranRepository: PembayaranRepository, fotoPembayaranRepository: FotoPembayaranRepository): DeleteAllPembayaranUseCase {
        return DeleteAllPembayaranUseCase(pembayaranRepository, fotoPembayaranRepository)
    }


    /**
     * Pembayaran Bulanan
     */
    @Provides
    fun provideGetListPembayaranBulananUseCase(
        pembayaranRepository: PembayaranRepository,
        baselinePembayaranRepository: BaselinePembayaranRepository,
        hargaKavlingRepository: HargaKavlingRepository,
        fotoPembayaranRepository: FotoPembayaranRepository,
        ambilKuitansiRepository: AmbilKuitansiRepository,
    ): GetListPembayaranBulananAsyncUseCase {
        return GetListPembayaranBulananAsyncUseCase(pembayaranRepository, baselinePembayaranRepository, hargaKavlingRepository, fotoPembayaranRepository, ambilKuitansiRepository)
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
    fun provideDeleteFotoPembayaranAsync(
        fotoPembayaranRepository: FotoPembayaranRepository,
        ambilKuitansiRepository: AmbilKuitansiRepository,
    )
        = DeleteFotoPembayaranAsyncUseCase(fotoPembayaranRepository, ambilKuitansiRepository)

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
     * Backup / Restore
     */
    @Provides
    fun provideGetListBackupUseCase(backupRestoreRepository: BackupRestoreRepository): GetListBackupAsyncUseCase {
        return GetListBackupAsyncUseCase(backupRestoreRepository)
    }

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
        imageSprRepository: ImageSprRepository,
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
            imageSprRepository,
            backupRestoreRepository,
        )
    }


    /**
     * Rekap Besar
     */
    @Provides
    fun provideCalculateRekapBesarAndGetRekapBesarOverview(
        pembayaranRepository: PembayaranRepository,
        dataDiriRepository: DataDiriRepository,
        hargaKavlingRepository: HargaKavlingRepository,
        feeMarketingRepository: FeeMarketingRepository,
        biayaMarketingRepository: BiayaMarketingRepository,
        biayaLainRepository: BiayaLainRepository,
        rekapBesarDetailRepository: RekapBesarDetailRepository,
    ): CalculateRekapBesarAndGetRekapBesarOverview {
        return CalculateRekapBesarAndGetRekapBesarOverview(
            pembayaranRepository,
            dataDiriRepository,
            hargaKavlingRepository,
            feeMarketingRepository,
            biayaMarketingRepository,
            biayaLainRepository,
            rekapBesarDetailRepository,
        )
    }

    @Provides
    fun provideGetRekapBesarDetailUseCase(rekapBesarDetailRepository: RekapBesarDetailRepository): GetRekapBesarDetailAsyncUseCase {
        return GetRekapBesarDetailAsyncUseCase(rekapBesarDetailRepository)
    }


    /**
     * Database User
     */
    @Provides
    fun provideGetAllDatabaseUserUseCase(databaseUserRepository: DatabaseUserRepository): GetAllDatabaseUserAsyncUseCase {
        return GetAllDatabaseUserAsyncUseCase(databaseUserRepository)
    }

    @Provides
    fun provideInsertDatabaseUserUseCase(databaseUserRepository: DatabaseUserRepository): InsertDatabaseUserAsyncUseCase {
        return InsertDatabaseUserAsyncUseCase(databaseUserRepository)
    }


    /**
     * Inden Booking
     */
    @Provides
    fun providesGetAllIndenBookingUseCase(
        indenBookingRepository: IndenBookingRepository,
        dataDiriRepository: DataDiriRepository,
        pembayaranRepository: PembayaranRepository,
        imageDataDiriRepository: ImageDataDiriRepository,
    ): GetAllIndenBookingAsyncUseCase {
        return GetAllIndenBookingAsyncUseCase(indenBookingRepository, dataDiriRepository, pembayaranRepository, imageDataDiriRepository)
    }

    // Inden Booking - Data Diri
    @Provides
    fun provideGetDataDiriIndenBookingUseCase(dataDiriRepository: DataDiriRepository): GetDataDiriIndenBookingAsyncUseCase {
        return GetDataDiriIndenBookingAsyncUseCase(dataDiriRepository)
    }
    @Provides
    fun provideInsertDataDiriIndenBookingUseCase(dataDiriRepository: DataDiriRepository): InsertDataDiriIndenBookingAsyncUseCase {
        return InsertDataDiriIndenBookingAsyncUseCase(dataDiriRepository)
    }
    @Provides
    fun provideEditDataDiriIndenBookingUseCase(dataDiriRepository: DataDiriRepository): EditDataDiriIndenBookingAsyncUseCase {
        return EditDataDiriIndenBookingAsyncUseCase(dataDiriRepository)
    }


    // Inden Booking - Foto Identitas / Image Data Diri
    @Provides
    fun provideGetFotoIdentitasIndenBookingAsyncUseCase(
        imageDataDiriRepository: ImageDataDiriRepository
    ): GetFotoIdentitasIndenBookingAsyncUseCase {
        return GetFotoIdentitasIndenBookingAsyncUseCase(imageDataDiriRepository)
    }

    // Inden Booking - Pembayaran
    @Provides
    fun provideGetAllPembayaranIndenBookingAsyncUseCase(
        indenBookingRepository: IndenBookingRepository,
        pembayaranRepository: PembayaranRepository
    ): GetAllPembayaranIndenBookingAsyncUseCase {
        return GetAllPembayaranIndenBookingAsyncUseCase(indenBookingRepository, pembayaranRepository)
    }

    @Provides
    fun provideGetHargaRumahIndenBookingAsyncUseCase(indenBookingRepository: IndenBookingRepository): GetHargaRumahIndenBookingAsyncUseCase {
        return GetHargaRumahIndenBookingAsyncUseCase(indenBookingRepository)
    }



    /**
     * Biaya Pribadi
     */
    @Provides
    fun provideGetAllBiayaPribadiUseCase(biayaPribadiRepository: BiayaPribadiRepository): GetAllBiayaPribadiAsyncUseCase {
        return GetAllBiayaPribadiAsyncUseCase(biayaPribadiRepository)
    }


    /**
     * Baseline Pembayaran
     */
    @Provides
    fun provideGetBaselinePembayaranByKavlingUseCase(baselinePembayaranRepository: BaselinePembayaranRepository): GetBaselinePembayaranByKavlingAsyncUseCase {
        return GetBaselinePembayaranByKavlingAsyncUseCase(baselinePembayaranRepository)
    }

    @Provides
    fun provideSetBaselinePembayaranUseCase(baselinePembayaranRepository: BaselinePembayaranRepository): SetBaselinePembayaranAsyncUseCase {
        return SetBaselinePembayaranAsyncUseCase(baselinePembayaranRepository)
    }


    /**
     * Progress Kavling
     */
    @Provides
    fun provideGetProgressKavlingUseCase(
        pembayaranRepository: PembayaranRepository,
        baselinePembayaranRepository: BaselinePembayaranRepository,
    ): GetProgressKavlingAsyncUseCase {
        return GetProgressKavlingAsyncUseCase(baselinePembayaranRepository, pembayaranRepository)
    }


    /**
     * Status Pembayaran
     */
    @Provides
    fun provideGetStatusPembayaranKavlingUseCase(statusPembayaranRepository: StatusPembayaranRepository): GetStatusPembayaranKavlingAsyncUseCase {
        return GetStatusPembayaranKavlingAsyncUseCase(statusPembayaranRepository)
    }


    /**
     * Promotion
     */
    @Provides
    fun provideGetPromotionMessageUseCase(promotionRepository: PromotionRepository): GetPromotionMessageAsyncUseCase {
        return GetPromotionMessageAsyncUseCase(promotionRepository)
    }


    /**
     * Ambil Kuitansi
     */
    @Provides
    fun provideInsertAmbilKuitansiUseCase(ambilKuitansiRepository: AmbilKuitansiRepository): InsertAmbilKuitansiAsyncUseCase {
        return InsertAmbilKuitansiAsyncUseCase(ambilKuitansiRepository)
    }
}