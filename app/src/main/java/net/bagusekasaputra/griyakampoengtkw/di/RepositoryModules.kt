package net.bagusekasaputra.griyakampoengtkw.di

import android.content.ContentResolver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import net.bagusekasaputra.griyakampoengtkw.data.CacheHelper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalBackupRestoreDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalBaselinePembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalBiayaLainDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalBiayaMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalBlockDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalFeeMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalFotoKuitansiDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalFotoPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalFotoPembayaranIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalHargaKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalHargaRumahIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalImageDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalImageDataDiriIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalImageSprDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalIndenBookingAmbilKuitansiDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalIndenBookingCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalKavlingCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalPembayaranTambahLuasanDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalPengembalianDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalPengingatDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalStandardAmbilKuitansiDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteAppUpdateSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteBackupRestoreDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteBaselinePembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteBiayaLainDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteBiayaMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteBlockDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteDatabaseUserDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteFeeMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteFotoPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteFotoPembayaranIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteHargaKavlingSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteHargaRumahIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteImageDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteImageDataDiriIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteImageSprDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteIndenBookingAmbilKuitansiDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteIndenBookingCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteKavlingCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemotePembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemotePembayaranTambahLuasanDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemotePengembalianDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemotePromotionDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteStandardAmbilKuitansiDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteStatusPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteTahapanDataSource
import net.bagusekasaputra.griyakampoengtkw.data.repository.AppUpdateRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.BaselinePembayaranRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.BiayaLainRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.BiayaMarketingRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.BiayaPribadiRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.BlockRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.DataDiriRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.DatabaseUserRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.DefaultBackupRestoreRepository
import net.bagusekasaputra.griyakampoengtkw.data.repository.FeeMarketingRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.FotoKuitansiRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.FotoPembayaranIndenBookingRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.FotoPembayaranRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.HargaKavlingRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.HargaRumahIndenBookingRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.ImageDataDiriIndenBookingRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.ImageDataDiriRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.ImageSprRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.IndenBookingAmbilKuitansiRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.IndenBookingCatatanPembayaranRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.IndenBookingRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.KavlingCatatanPembayaranRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.KavlingRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.PembayaranTambahLuasanRepositoryImplD
import net.bagusekasaputra.griyakampoengtkw.data.repository.PengembalianRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.PengingatRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.PromotionRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.RekapBesarDetailRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.StandardAmbilKuitansiRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.StatusPembayaranRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.TahapanRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.pembayaran.DefaultPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.data.repository.pembayaran.LegacyPembayaranRepository
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
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranTambahLuasanRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PengembalianRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PengingatRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PromotionRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.RekapBesarDetailRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.StandardAmbilKuitansiRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.StatusPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.TahapanRepository
import java.io.File

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModules {

    /**
     * Tahapan
     */
    @Provides
    fun provideTahapanRepository(remoteTahapanDataSource: RemoteTahapanDataSource): TahapanRepository {
        return TahapanRepositoryImpl(remoteTahapanDataSource)
    }


    /**
     * Block
     */
    @Provides
    fun provideBlockRepository(
        localBlockDataSource: LocalBlockDataSource,
        remoteBlockDataSource: RemoteBlockDataSource,
        cacheHelper: CacheHelper,
    ): BlockRepository {
        return BlockRepositoryImpl(localBlockDataSource, remoteBlockDataSource, cacheHelper)
    }


    /**
     * Kavling
     */
    @Provides
    fun provideKavlingRepository(
        localKavlingDataSource: LocalKavlingDataSource,
        remoteKavlingDataSource: RemoteKavlingDataSource,
        localBlockDataSource: LocalBlockDataSource,
        cacheHelper: CacheHelper
    ): KavlingRepository {
        return KavlingRepositoryImpl(localKavlingDataSource, remoteKavlingDataSource, localBlockDataSource, cacheHelper)
    }


    /**
     * Data Diri
     */
    @Provides
    fun provideDataDiriRepository(
        localDataDiriDataSource: LocalDataDiriDataSource,
        remoteDataDiriDataSource: RemoteDataDiriDataSource,
        remoteKavlingDataSource: RemoteKavlingDataSource,
        localMetadata: LocalMetadataDataSource,
        remoteMetadata: RemoteMetadataDataSource,
        cacheHelper: CacheHelper,
    ): DataDiriRepository {
        return DataDiriRepositoryImpl(
            localDataDiriDataSource,
            remoteDataDiriDataSource,
            remoteKavlingDataSource,
            localMetadata,
            remoteMetadata,
            cacheHelper,
        )
    }


    /**
     * Pembayaran
     */
    @Legacy
    @Provides
    fun provideLegacyPembayaranRepository(
        localPembayaranDataSource: LocalPembayaranDataSource,
        remotePembayaranDataSource: RemotePembayaranDataSource,
        localMetadata: LocalMetadataDataSource,
        remoteMetadata: RemoteMetadataDataSource,
        cacheHelper: CacheHelper,
    ): PembayaranRepository {
        return LegacyPembayaranRepository(
            localPembayaranDataSource,
            remotePembayaranDataSource,
            localMetadata,
            remoteMetadata,
            cacheHelper,
        )
    }
    
    @Default
    @Provides
    fun provideDefaultPembayaranRepository(
        localDataSource: LocalPembayaranDataSource,
        remoteDataSource: RemotePembayaranDataSource,
        cacheHelper: CacheHelper,
    ): PembayaranRepository {
        return DefaultPembayaranRepository(localDataSource, remoteDataSource, cacheHelper)
    }


    /**
     * Harga Kavling
     */
    @Provides
    fun provideHargaKavlingRepository(
        localHargaKavlingDataSource: LocalHargaKavlingDataSource,
        remoteHargaKavlingSource: RemoteHargaKavlingSource,
        localMetadata: LocalMetadataDataSource,
        remoteMetadata: RemoteMetadataDataSource,
    ): HargaKavlingRepository {
        return HargaKavlingRepositoryImpl(
            localHargaKavlingDataSource,
            remoteHargaKavlingSource,
            localMetadata,
            remoteMetadata
        )
    }


    /**
     * App Update
     */
    @Provides
    fun provideAppUpdateRepository(remoteAppUpdateSource: RemoteAppUpdateSource): AppUpdateRepository {
        return AppUpdateRepositoryImpl(remoteAppUpdateSource)
    }


    // Image Data Diri
    @Provides
    fun provideImageDataDiriRepository(
        localImageDataDiriDataSource: LocalImageDataDiriDataSource,
        remoteImageDataDiriDataSource: RemoteImageDataDiriDataSource,
        localMetadataDataSource: LocalMetadataDataSource,
        remoteMetadataDataSource: RemoteMetadataDataSource,
        @ExternalDir externalFileDir: File?,
        contentResolver: ContentResolver,
        cacheHelper: CacheHelper,
    ): ImageDataDiriRepository {
        return ImageDataDiriRepositoryImpl(
            localImageDataDiriDataSource,
            remoteImageDataDiriDataSource,
            localMetadataDataSource,
            remoteMetadataDataSource,
            externalFileDir,
            contentResolver,
            cacheHelper,
        )
    }


    // Foto Kuitansi
    @Provides
    fun provideFotoKuitansiRepository(localFotoKuitansiDataSource: LocalFotoKuitansiDataSource, contentResolver: ContentResolver): FotoKuitansiRepository {
        return FotoKuitansiRepositoryImpl(localFotoKuitansiDataSource, contentResolver)
    }


    // Image SPR
    @Provides
    fun provideImageSprRepository(
        localImageSprDataSource: LocalImageSprDataSource,
        remoteImageSprDataSource: RemoteImageSprDataSource,
        localMetadata: LocalMetadataDataSource,
        remoteMetadata: RemoteMetadataDataSource,
        contentResolver: ContentResolver
    ): ImageSprRepository {
        return ImageSprRepositoryImpl(localImageSprDataSource, remoteImageSprDataSource, localMetadata, remoteMetadata, contentResolver)
    }


    /**
     * FeeMarketing
     */
    @Provides
    fun provideFeeMarketingRepository(
        localFeeMarketingDataSource: LocalFeeMarketingDataSource,
        remoteFeeMarketingDataSource: RemoteFeeMarketingDataSource,
        localMetadata: LocalMetadataDataSource,
        remoteMetadata: RemoteMetadataDataSource,
    ): FeeMarketingRepository {
        return FeeMarketingRepositoryImpl(localFeeMarketingDataSource, remoteFeeMarketingDataSource, localMetadata, remoteMetadata)
    }


    /**
     * Biaya Marketing
     */
    @Provides
    fun provideBiayaMarketingRepository(
        localBiayaMarketingDataSource: LocalBiayaMarketingDataSource,
        remoteBiayaMarketDataSource: RemoteBiayaMarketingDataSource,
        localMetadata: LocalMetadataDataSource,
        remoteMetadata: RemoteMetadataDataSource,
    ): BiayaMarketingRepository {
        return BiayaMarketingRepositoryImpl(localBiayaMarketingDataSource, remoteBiayaMarketDataSource, localMetadata, remoteMetadata)
    }


    /**
     * Kavling Catatan Pembayaran
     */
    @Provides
    fun provideKavlingCatatanPembayaranRepository(
        localKavlingCatatanPembayaranDataSource: LocalKavlingCatatanPembayaranDataSource,
        remoteKavlingCatatanPembayaranDataSource: RemoteKavlingCatatanPembayaranDataSource,
    ): KavlingCatatanPembayaranRepository {
        return KavlingCatatanPembayaranRepositoryImpl(localKavlingCatatanPembayaranDataSource, remoteKavlingCatatanPembayaranDataSource)
    }

    /**
     * Inden Booking Catatan Pembayaran
     */
    @Provides
    fun provideIndenBookingCatatanPembayaranRepository(
        localDataSource: LocalIndenBookingCatatanPembayaranDataSource,
        remoteDataSource: RemoteIndenBookingCatatanPembayaranDataSource,
        cacheHelper: CacheHelper,
    ): IndenBookingCatatanPembayaranRepository {
        return IndenBookingCatatanPembayaranRepositoryImpl(localDataSource, remoteDataSource, cacheHelper)
    }



    /**
     * Foto Pembayaran
     */
    @Provides
    fun provideFotoPembayaranRepository(
        @DataSourceModule.RoomDatabase roomDataSource: LocalFotoPembayaranDataSource,
        @DataSourceModule.DeviceStorage deviceStorageDataSource: LocalFotoPembayaranDataSource,
        remoteFotoPembayaranDataSource: RemoteFotoPembayaranDataSource,
        localMetadataDataSource: LocalMetadataDataSource,
        remoteMetadataDataSource: RemoteMetadataDataSource,
    ): FotoPembayaranRepository {
        return FotoPembayaranRepositoryImpl(
            roomDataSource,
            deviceStorageDataSource,
            remoteFotoPembayaranDataSource,
            localMetadataDataSource,
            remoteMetadataDataSource,
        )
    }

    /**
     * Pengingat
     */
    @Provides
    fun providePengingatRepository(localPengingatDataSource: LocalPengingatDataSource): PengingatRepository {
        return PengingatRepositoryImpl(localPengingatDataSource)
    }

    /**
     * Biaya Lain
     */
    @Provides
    fun provideBiayaLainRepository(
        localBiayaLainDataSource: LocalBiayaLainDataSource,
        remoteBiayaLainDataSource: RemoteBiayaLainDataSource,
        localMetadataDataSource: LocalMetadataDataSource,
        remoteMetadataDataSource: RemoteMetadataDataSource,
    ): BiayaLainRepository {
        return BiayaLainRepositoryImpl(
            localBiayaLainDataSource,
            remoteBiayaLainDataSource,
            localMetadataDataSource,
            remoteMetadataDataSource,
        )
    }

    /**
     * Backup / Restore
     */
    @Provides
    fun provideBackupRestoreRepository(
        localDataSource: LocalBackupRestoreDataSource,
        remoteDataSource: RemoteBackupRestoreDataSource,
        cacheHelper: CacheHelper
    ): BackupRestoreRepository {
        return DefaultBackupRestoreRepository(localDataSource, remoteDataSource, cacheHelper)
    }


    /**
     * Rekap Besar Detail
     */
    @Provides
    fun provideRekapBesarDetailRepository(@InternalDir internalFile: File): RekapBesarDetailRepository {
        return RekapBesarDetailRepositoryImpl(internalFile)
    }

    /**
     * Database Use
     */
    @Provides
    fun provideDatabaseUserRepository(remoteDatabaseUserDataSource: RemoteDatabaseUserDataSource): DatabaseUserRepository {
        return DatabaseUserRepositoryImpl(remoteDatabaseUserDataSource)
    }

    /**
     * Biaya Pribadi
     */
    @Provides
    fun provideBiayaPribadiRepository(): BiayaPribadiRepository {
        return BiayaPribadiRepositoryImpl()
    }

    /**
     * Baseline Pembayaran
     */
    @Provides
    fun provideBaselinePembayaranRepository(
        localDataSource: LocalBaselinePembayaranDataSource,
        remoteDataSource: RemoteBaselinePembayaranDataSource,
        localMetadata: LocalMetadataDataSource,
        remoteMetadata: RemoteMetadataDataSource,
    ): BaselinePembayaranRepository {
        return BaselinePembayaranRepositoryImpl(
            localDataSource,
            remoteDataSource,
            localMetadata,
            remoteMetadata,
        )
    }


    /**
     * Inden Booking
     */
    @Provides
    fun provideIndenBookingRepository(
        localDataSource: LocalIndenBookingDataSource,
        remoteDataSource: RemoteIndenBookingDataSource,
        localMetadata: LocalMetadataDataSource,
        remoteMetadata: RemoteMetadataDataSource,
    ): IndenBookingRepository {
        return IndenBookingRepositoryImpl(localDataSource, remoteDataSource, localMetadata, remoteMetadata)
    }


    /**
     * Status Pembayaran
     */
    @Provides
    fun provideStatusPembayaranRepository(
        remoteStatusPembayaranDataSource: RemoteStatusPembayaranDataSource,
    ): StatusPembayaranRepository {
        return StatusPembayaranRepositoryImpl(remoteStatusPembayaranDataSource)
    }

    /**
     * Promotion
     */
    @Provides
    fun providePromotionRepository(remotePromotionDataSource: RemotePromotionDataSource): PromotionRepository {
        return PromotionRepositoryImpl(remotePromotionDataSource)
    }

    /**
     * Standard Ambil Kuitansi
     */
    @Provides
    fun provideStandardAmbilKuitansiRepository(
        localDataSource: LocalStandardAmbilKuitansiDataSource,
        remoteDataSource: RemoteStandardAmbilKuitansiDataSource,
        cacheHelper: CacheHelper,
    ): StandardAmbilKuitansiRepository {
        return StandardAmbilKuitansiRepositoryImpl(localDataSource, remoteDataSource, cacheHelper)
    }

    @Provides
    fun provideIndenBookingAmbilKuitansiRepository(
        localDataSource: LocalIndenBookingAmbilKuitansiDataSource,
        remoteDataSource: RemoteIndenBookingAmbilKuitansiDataSource,
        cacheHelper: CacheHelper,
    ): IndenBookingAmbilKuitansiRepository {
        return IndenBookingAmbilKuitansiRepositoryImpl(localDataSource, remoteDataSource, cacheHelper)
    }


    /**
     * Harga Rumah Inden Booking
     */
    @Provides
    fun provideHargaRumahIndenBookingRepository(
        localDataSource: LocalHargaRumahIndenBookingDataSource,
        remoteDataSource: RemoteHargaRumahIndenBookingDataSource,
        cacheHelper: CacheHelper,
    ): HargaRumahIndenBookingRepository {
        return HargaRumahIndenBookingRepositoryImpl(localDataSource, remoteDataSource, cacheHelper)
    }

    /**
     * Foto Pembayaran Inden Booking
     */
    @Provides
    fun provideFotoPembayaranIndenBookingRepository(
        localDataSource: LocalFotoPembayaranIndenBookingDataSource,
        remoteDataSource: RemoteFotoPembayaranIndenBookingDataSource,
        @ExternalDir externalStorageFile: File?,
        cacheHelper: CacheHelper,
    ): FotoPembayaranIndenBookingRepository {
        return FotoPembayaranIndenBookingRepositoryImpl(localDataSource, remoteDataSource, externalStorageFile, cacheHelper)
    }

    /**
     * Image Data Diri Inden Booking
     */
    @Provides
    fun provideImageDataDiriIndenBookingRepository(
        localDataSource: LocalImageDataDiriIndenBookingDataSource,
        remoteDataSource: RemoteImageDataDiriIndenBookingDataSource,
        @ExternalDir externalFileDir: File?,
        cacheHelper: CacheHelper
    ): ImageDataDiriIndenBookingRepository {
        return ImageDataDiriIndenBookingRepositoryImpl(localDataSource, remoteDataSource, externalFileDir, cacheHelper)
    }

    /**
     * Pengembalian Pembayaran
     */
    @Provides
    fun providePengembalianRepository(
        localDataSource: LocalPengembalianDataSource,
        remoteDataSource: RemotePengembalianDataSource,
        @ExternalDir externalFileDir: File?,
        cacheHelper: CacheHelper,
    ): PengembalianRepository {
        return PengembalianRepositoryImpl(localDataSource, remoteDataSource, cacheHelper, externalFileDir)
    }

    /**
     * Pembayaran Tambahan Luasan
     */
    @Provides
    fun providePembayaranTambahanLuasanRepository(
        localSource: LocalPembayaranTambahLuasanDataSource,
        remoteSource: RemotePembayaranTambahLuasanDataSource,
        localMetadata: LocalMetadataDataSource,
        remoteMetadata: RemoteMetadataDataSource,
    ): PembayaranTambahLuasanRepository {
        return PembayaranTambahLuasanRepositoryImplD(
            localSource, remoteSource, localMetadata, remoteMetadata
        )
    }
}