package net.bagusekasaputra.griyakampoengtkw.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import net.bagusekasaputra.griyakampoengtkw.cache.CacheInitializer
import net.bagusekasaputra.griyakampoengtkw.cache.DefaultCacheInitializer
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.ambilKuitansi.InsertAmbilKuitansiAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.backupRestore.CreateBackupAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.backupRestore.GetListBackupAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.baselinePembayaran.GetBaselinePembayaranByKavlingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.baselinePembayaran.SetBaselinePembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaLain.AddBiayaLainAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaLain.DeleteBiayaLainAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaLain.GetAllBiayaLainAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaLain.UpdateBiayaLainAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaMarketing.GetAllBiayaMarketingByKavlingKodeAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.biayaPribadi.GetAllBiayaPribadiAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.block.GetAllBlocksAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.catatanPembayaran.AddCatatanPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.catatanPembayaran.DeleteCatatanPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.catatanPembayaran.GetCatatanPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.catatanPembayaran.UpdateCatatanPembayaranAsyncUseCase
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
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.dataDiri.EditDataDiriIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.dataDiri.GetDataDiriIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.dataDiri.InsertDataDiriIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.fotoPembayaran.DeleteFotoPembayaranIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.fotoPembayaran.GetFotoPembayaranIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.fotoPembayaran.InsertFotoPembayaranIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.hargaRumah.GetHargaRumahIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.hargaRumah.UpdateHargaRumahIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.imageDataDiri.DeleteFotoIdentitasIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.imageDataDiri.GetImageDataDiriIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.imageDataDiri.InsertFotoIdentitasIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.imageDataDiri.UpdateFotoIdentitasIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.pembayaran.GetAllPembayaranIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.pembayaran.InsertPembayaranIndenBookingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetKavlingAndProgressStreamAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetKavlingByBlockAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling.GetListUnmigratedKavlingsAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran.DeletePembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran.GetListPembayaranBulananAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran.GetSinglePembayaranByKavlingAndTerminAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran.InsertPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran.UpdatePembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pengembalian.GetPengembalianStreamAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pengingat.AddPengingatAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pengingat.DeletePengingatAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pengingat.GetAllPengingatAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pengingat.TurnOnOffPengingatAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pengingat.UpdatePengingatAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.promotion.GetPromotionMessageAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetListRekapGlobalAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetRekapBesarDetailAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetRekapBesarOverviewAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.rekap.GetRekapGlobalStreamAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.reportKavling.GetAllReportKavlingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.statusPembayaran.GetStatusPembayaranKavlingAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.tambahanPembayaran.AddTambahanPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.tambahanPembayaran.DeleteTambahanPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.tambahanPembayaran.GetTambahanPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.tambahanPembayaran.GetTambahanPembayaranByIdAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.tambahanPembayaran.UpdateTambahanPembayaranAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.repository.AppUpdateRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BackupRestoreRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BaselinePembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaLainRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMarketingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaPribadiRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DatabaseUserRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FeeMarketingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoKuitansiRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoPembayaranIndenBookingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaRumahIndenBookingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageDataDiriIndenBookingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageDataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageSprRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingAmbilKuitansiRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingCatatanPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingCatatanPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PengembalianRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PengingatRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PromotionRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.RekapBesarDetailRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.StandardAmbilKuitansiRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.StatusPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.TambahanPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.appupdate.GetUpdateInformationUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.biayaMarketing.AddBiayaMarketingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.biayaMarketing.DeleteAllBiayaMarketingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.biayaMarketing.DeleteSingleBiayaMarketingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.biayaMarketing.EditBiayaMarketingUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.block.AddNewBlockUseCase
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
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.pembayaran.AddPembayaranUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.pembayaran.DeleteAllPembayaranUseCase
import java.io.File

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun provideCacheInitializer(sharedPrefs: SharedPreferences): CacheInitializer {
        return DefaultCacheInitializer(sharedPrefs)
    }

    @Provides
    fun provideCoroutineDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }

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
    fun provideGetKavlingsByBlockAsync(
        kavlingRepository: KavlingRepository,
    )
        = GetKavlingByBlockAsyncUseCase(kavlingRepository)

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
     * KavlingAndProgress
     */
    @Provides
    fun provideGetKavlingAndProgressStreamUseCase(
        kavlingRepository: KavlingRepository,
        @Default pembayaranRepository: PembayaranRepository,
        baselineRepository: BaselinePembayaranRepository
    ) = GetKavlingAndProgressStreamAsyncUseCase(kavlingRepository, pembayaranRepository, baselineRepository)


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
    fun provideGetSinglePembayaranUseCase(@Legacy pembayaranRepository: PembayaranRepository): GetSinglePembayaranByKavlingAndTerminAsyncUseCase {
        return GetSinglePembayaranByKavlingAndTerminAsyncUseCase(pembayaranRepository)
    }

    @Provides
    fun provideInsertPembayaranUseCase(@Legacy pembayaranRepository: PembayaranRepository): InsertPembayaranAsyncUseCase {
        return InsertPembayaranAsyncUseCase(pembayaranRepository)
    }

    @Provides
    fun provideAddPembayaranUseCase(@Legacy pembayaranRepository: PembayaranRepository): AddPembayaranUseCase {
        return AddPembayaranUseCase(pembayaranRepository)
    }

    @Provides
    fun provideUpdatePembayaranUseCase(@Legacy pembayaranRepository: PembayaranRepository): UpdatePembayaranAsyncUseCase {
        return UpdatePembayaranAsyncUseCase(pembayaranRepository)
    }

    @Provides
    fun provideDeletePembayaranUseCase(
        @Legacy pembayaranRepository: PembayaranRepository,
        fotoPembayaranRepository: FotoPembayaranRepository,
        standardAmbilKuitansiRepository: StandardAmbilKuitansiRepository,
    ): DeletePembayaranAsyncUseCase {
        return DeletePembayaranAsyncUseCase(pembayaranRepository, fotoPembayaranRepository, standardAmbilKuitansiRepository)
    }

    @Provides
    fun provideDeleteAllPembayaran(
        @Legacy pembayaranRepository: PembayaranRepository,
        fotoPembayaranRepository: FotoPembayaranRepository
    ): DeleteAllPembayaranUseCase {
        return DeleteAllPembayaranUseCase(pembayaranRepository, fotoPembayaranRepository)
    }


    /**
     * Pembayaran Bulanan
     */
    @Provides
    fun provideGetListPembayaranBulananUseCase(
        @Legacy pembayaranRepository: PembayaranRepository,
        baselinePembayaranRepository: BaselinePembayaranRepository,
        hargaKavlingRepository: HargaKavlingRepository,
        fotoPembayaranRepository: FotoPembayaranRepository,
        standardAmbilKuitansiRepository: StandardAmbilKuitansiRepository,
    ): GetListPembayaranBulananAsyncUseCase {
        return GetListPembayaranBulananAsyncUseCase(pembayaranRepository, baselinePembayaranRepository, hargaKavlingRepository, fotoPembayaranRepository, standardAmbilKuitansiRepository)
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
    fun provideGetCatatanPembayaran(
        kavlingCatatanPembayaranRepository: KavlingCatatanPembayaranRepository,
        indenBookingCatatanPembayaranRepository: IndenBookingCatatanPembayaranRepository,
    )
        = GetCatatanPembayaranAsyncUseCase(kavlingCatatanPembayaranRepository, indenBookingCatatanPembayaranRepository)

    @Provides
    fun provideAddCatatanPembayaranUseCase(
        kavlingCatatanPembayaranRepository: KavlingCatatanPembayaranRepository,
        indenBookingCatatanPembayaranRepository: IndenBookingCatatanPembayaranRepository,
    )
        = AddCatatanPembayaranAsyncUseCase(kavlingCatatanPembayaranRepository, indenBookingCatatanPembayaranRepository)

    @Provides
    fun provideUpdateCatatanPembayaranUseCase(
        kavlingCatatanPembayaranRepository: KavlingCatatanPembayaranRepository,
        indenBookingCatatanPembayaranRepository: IndenBookingCatatanPembayaranRepository,
    )
        = UpdateCatatanPembayaranAsyncUseCase(kavlingCatatanPembayaranRepository, indenBookingCatatanPembayaranRepository)

    @Provides
    fun provideDeleteCatatanPembayaranUseCase(
        kavlingCatatanPembayaranRepository: KavlingCatatanPembayaranRepository,
        indenBookingCatatanPembayaranRepository: IndenBookingCatatanPembayaranRepository,
    )
        = DeleteCatatanPembayaranAsyncUseCase(kavlingCatatanPembayaranRepository, indenBookingCatatanPembayaranRepository)

    /**
     * Image Data Diri
     */
    @Provides
    fun provideGetImageDataDiriByKavlingKodeUseCase(imageDataDiriRepository: ImageDataDiriRepository)
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
        standardAmbilKuitansiRepository: StandardAmbilKuitansiRepository,
    )
        = DeleteFotoPembayaranAsyncUseCase(fotoPembayaranRepository, standardAmbilKuitansiRepository)

    @Provides
    fun provideIsFotoPembayaranExistAsync(fotoPembayaranRepository: FotoPembayaranRepository)
        = IsFotoPembayaranExistAsyncUseCase(fotoPembayaranRepository)


    /**
     * Report Kavling
     */
    @Provides
    fun provideGetAllReportKavlingUseCase(
        @Legacy pembayaranRepository: PembayaranRepository,
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
        blockRepository: BlockRepository,
        kavlingRepository: KavlingRepository,
        dataDiriRepository: DataDiriRepository,
        @Legacy pembayaranRepository: PembayaranRepository,
        hargaKavlingRepository: HargaKavlingRepository,
    ): GetListRekapGlobalAsyncUseCase {
        return GetListRekapGlobalAsyncUseCase(blockRepository, kavlingRepository, dataDiriRepository, pembayaranRepository, hargaKavlingRepository)
    }

    @Provides
    fun provideGetRekapGlobalStreamUseCase(
        blockRepository: BlockRepository,
        kavlingRepository: KavlingRepository,
        dataDiriRepository: DataDiriRepository,
        @Legacy pembayaranRepository: PembayaranRepository,
        hargaKavlingRepository: HargaKavlingRepository,
    ) = GetRekapGlobalStreamAsyncUseCase(blockRepository, kavlingRepository, dataDiriRepository, pembayaranRepository, hargaKavlingRepository)


    /**
     * Backup / Restore
     */
    @Provides
    fun provideGetListBackupUseCase(backupRestoreRepository: BackupRestoreRepository): GetListBackupAsyncUseCase {
        return GetListBackupAsyncUseCase(backupRestoreRepository)
    }

    @Provides
    fun provideCreateBackupUseCase(): CreateBackupAsyncUseCase {
        return CreateBackupAsyncUseCase()
    }


    /**
     * Rekap Besar
     */
    @Provides
    fun provideCalculateRekapBesarAndGetRekapBesarOverview(
        blockRepository: BlockRepository,
        kavlingRepository: KavlingRepository,
        @Legacy pembayaranRepository: PembayaranRepository,
        dataDiriRepository: DataDiriRepository,
        hargaKavlingRepository: HargaKavlingRepository,
        feeMarketingRepository: FeeMarketingRepository,
        biayaMarketingRepository: BiayaMarketingRepository,
        biayaLainRepository: BiayaLainRepository,
        rekapBesarDetailRepository: RekapBesarDetailRepository,
    ): GetRekapBesarOverviewAsyncUseCase {
        return GetRekapBesarOverviewAsyncUseCase(blockRepository, kavlingRepository, pembayaranRepository, dataDiriRepository, hargaKavlingRepository, feeMarketingRepository, biayaMarketingRepository, biayaLainRepository, rekapBesarDetailRepository)
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
        @Legacy pembayaranRepository: PembayaranRepository,
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
    fun provideInsertFotoIdentitasIndenBookingUseCase(
        imageDataDiriRepository: ImageDataDiriRepository,
    ): InsertFotoIdentitasIndenBookingAsyncUseCase {
        return InsertFotoIdentitasIndenBookingAsyncUseCase(imageDataDiriRepository)
    }
    @Provides
    fun provideUpdateFotoIdentitasIndenBookingUseCase(
        imageDataDiriRepository: ImageDataDiriRepository,
    ): UpdateFotoIdentitasIndenBookingAsyncUseCase {
        return UpdateFotoIdentitasIndenBookingAsyncUseCase(imageDataDiriRepository)
    }
    @Provides
    fun provideDeleteFotoIdentitasIndenBookingUseCase(
        imageDataDiriRepository: ImageDataDiriRepository,
    ): DeleteFotoIdentitasIndenBookingAsyncUseCase {
        return DeleteFotoIdentitasIndenBookingAsyncUseCase(imageDataDiriRepository)
    }


    // Inden Booking - Pembayaran
    @Provides
    fun provideGetAllPembayaranIndenBookingAsyncUseCase(
        hargaRumahIndenBookingRepository: HargaRumahIndenBookingRepository,
        @Legacy pembayaranRepository: PembayaranRepository,
        fotoPembayaranRepository: FotoPembayaranIndenBookingRepository,
        ambilKuitansiRepository: IndenBookingAmbilKuitansiRepository,
    ): GetAllPembayaranIndenBookingAsyncUseCase {
        return GetAllPembayaranIndenBookingAsyncUseCase(hargaRumahIndenBookingRepository, pembayaranRepository, fotoPembayaranRepository, ambilKuitansiRepository)
    }
    @Provides
    fun provideInsertPembayaranIndenBookingUseCase(@Legacy pembayaranRepository: PembayaranRepository): InsertPembayaranIndenBookingAsyncUseCase {
        return InsertPembayaranIndenBookingAsyncUseCase(pembayaranRepository)
    }


    // Inden Booking - Harga Rumah
    @Provides
    fun provideGetHargaRumahIndenBookingAsyncUseCase(hargaRumahIndenBookingRepository: HargaRumahIndenBookingRepository): GetHargaRumahIndenBookingAsyncUseCase {
        return GetHargaRumahIndenBookingAsyncUseCase(hargaRumahIndenBookingRepository)
    }
    @Provides
    fun provideUpdateHargaRumahIndenBookingUseCase(hargaRumahIndenBookingRepository: HargaRumahIndenBookingRepository): UpdateHargaRumahIndenBookingAsyncUseCase {
        return UpdateHargaRumahIndenBookingAsyncUseCase(hargaRumahIndenBookingRepository)
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
    fun provideInsertAmbilKuitansiUseCase(
        standardAmbilKuitansiRepository: StandardAmbilKuitansiRepository,
        indenBookingAmbilKuitansiRepository: IndenBookingAmbilKuitansiRepository,
    ): InsertAmbilKuitansiAsyncUseCase {
        return InsertAmbilKuitansiAsyncUseCase(standardAmbilKuitansiRepository, indenBookingAmbilKuitansiRepository)
    }


    /**
     * Foto Pembayaran Inden Booking
     */
    @Provides
    fun provideGetFotoPembayaranIndenBookingUseCase(fotoPembayaranIndenBookingRepository: FotoPembayaranIndenBookingRepository): GetFotoPembayaranIndenBookingAsyncUseCase {
        return GetFotoPembayaranIndenBookingAsyncUseCase(fotoPembayaranIndenBookingRepository)
    }

    @Provides
    fun provideInsertFotoPembayaranIndenBookingUseCase(fotoPembayaranRepository: FotoPembayaranIndenBookingRepository): InsertFotoPembayaranIndenBookingAsyncUseCase {
        return InsertFotoPembayaranIndenBookingAsyncUseCase(fotoPembayaranRepository)
    }

    @Provides
    fun provideDeleteFotoPembayaranIndenBookingUseCase(fotoPembayaranRepository: FotoPembayaranIndenBookingRepository): DeleteFotoPembayaranIndenBookingAsyncUseCase {
        return DeleteFotoPembayaranIndenBookingAsyncUseCase(fotoPembayaranRepository)
    }


    /**
     * Image Data Diri Inden Booking
     */
    @Provides
    fun provideGetImageDataDiriIndenBookingUseCase(imageDataDiriRepository: ImageDataDiriIndenBookingRepository): GetImageDataDiriIndenBookingAsyncUseCase {
        return GetImageDataDiriIndenBookingAsyncUseCase(imageDataDiriRepository)
    }

    /**
     * Pengembalian Pembayaran
     */
    @Provides
    fun provideGetPengembalianStreamUseCase(pengembalianRepository: PengembalianRepository)
        = GetPengembalianStreamAsyncUseCase(pengembalianRepository)

    /**
     * Tambahan Pembayaran
     */
    @Provides
    fun provideGetTambahanPembayaranByKavling(
        tambahanPembayaranRepository: TambahanPembayaranRepository
    ): GetTambahanPembayaranAsyncUseCase {
        return GetTambahanPembayaranAsyncUseCase(tambahanPembayaranRepository)
    }

    @Provides
    fun provideAddTambahanPembayaran(
        tambahanPembayaranRepository: TambahanPembayaranRepository
    ): AddTambahanPembayaranAsyncUseCase {
        return AddTambahanPembayaranAsyncUseCase(tambahanPembayaranRepository)
    }

    @Provides
    fun provideGetTambahPembayaranById(
        tambahanPembayaranRepository: TambahanPembayaranRepository
    ): GetTambahanPembayaranByIdAsyncUseCase {
        return GetTambahanPembayaranByIdAsyncUseCase(tambahanPembayaranRepository)
    }

    @Provides
    fun provideUpdateTambahanPembayaran(
        tambahanPembayaranRepository: TambahanPembayaranRepository
    ): UpdateTambahanPembayaranAsyncUseCase {
        return UpdateTambahanPembayaranAsyncUseCase(tambahanPembayaranRepository)
    }

    @Provides
    fun provideDeleteTambahanPembayaran(
        tambahanPembayaranRepository: TambahanPembayaranRepository
    ): DeleteTambahanPembayaranAsyncUseCase {
        return DeleteTambahanPembayaranAsyncUseCase(tambahanPembayaranRepository)
    }
}