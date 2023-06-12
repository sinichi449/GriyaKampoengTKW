package net.bagusekasaputra.griyakampoengtkw.di

import android.content.ContentResolver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.bagusekasaputra.griyakampoengtkw.data.CacheHelper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.*
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.*
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.*
import net.bagusekasaputra.griyakampoengtkw.data.repository.*
import net.bagusekasaputra.griyakampoengtkw.data.repository.DefaultBackupRestoreRepository
import net.bagusekasaputra.griyakampoengtkw.data.repository.pembayaran.DefaultPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.data.repository.pembayaran.LegacyPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.*
import java.io.File
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

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
        backupBlokDataSource: BackupBlokDataSource,
    ): BlockRepository {
        return BlockRepositoryImpl(localBlockDataSource, remoteBlockDataSource, backupBlokDataSource)
    }


    /**
     * Kavling
     */
    @Provides
    fun provideKavlingRepository(
        localKavlingDataSource: LocalKavlingDataSource,
        remoteKavlingDataSource: RemoteKavlingDataSource,
        backupKavlingDataSource: BackupKavlingDataSource,
        localBlockDataSource: LocalBlockDataSource,
        cacheHelper: CacheHelper
    ): KavlingRepository {
        return KavlingRepositoryImpl(localKavlingDataSource, remoteKavlingDataSource, backupKavlingDataSource, localBlockDataSource, cacheHelper)
    }


    /**
     * Data Diri
     */
    @Provides
    fun provideDataDiriRepository(
        localDataDiriDataSource: LocalDataDiriDataSource,
        remoteDataDiriDataSource: RemoteDataDiriDataSource,
        remoteKavlingDataSource: RemoteKavlingDataSource,
        backupDataDiriDataSource: BackupDataDiriDataSource,
        localMetadata: LocalMetadataDataSource,
        remoteMetadata: RemoteMetadataDataSource,
        cacheHelper: CacheHelper,
    ): DataDiriRepository {
        return DataDiriRepositoryImpl(
            localDataDiriDataSource,
            remoteDataDiriDataSource,
            remoteKavlingDataSource,
            backupDataDiriDataSource,
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
        backupPembayaranDataSource: BackupPembayaranDataSource,
        localMetadata: LocalMetadataDataSource,
        remoteMetadata: RemoteMetadataDataSource,
        cacheHelper: CacheHelper,
    ): PembayaranRepository {
        return LegacyPembayaranRepository(
            localPembayaranDataSource,
            remotePembayaranDataSource,
            backupPembayaranDataSource,
            localMetadata,
            remoteMetadata,
            cacheHelper,
        )
    }

    @Singleton
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
        backupHargaKavlingDataSource: BackupHargaKavlingDataSource,
        localMetadata: LocalMetadataDataSource,
        remoteMetadata: RemoteMetadataDataSource,
    ): HargaKavlingRepository {
        return HargaKavlingRepositoryImpl(
            localHargaKavlingDataSource,
            remoteHargaKavlingSource,
            backupHargaKavlingDataSource,
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
        backupImageDataDiriDataSource: BackupImageDataDiriDataSource,
        localMetadataDataSource: LocalMetadataDataSource,
        remoteMetadataDataSource: RemoteMetadataDataSource,
        @ExternalDir externalFileDir: File?,
        contentResolver: ContentResolver,
        cacheHelper: CacheHelper,
    ): ImageDataDiriRepository {
        return ImageDataDiriRepositoryImpl(
            localImageDataDiriDataSource,
            remoteImageDataDiriDataSource,
            backupImageDataDiriDataSource,
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
        backupImageSPRDataSource: BackupImageSPRDataSource,
        localMetadata: LocalMetadataDataSource,
        remoteMetadata: RemoteMetadataDataSource,
        contentResolver: ContentResolver
    ): ImageSprRepository {
        return ImageSprRepositoryImpl(localImageSprDataSource, remoteImageSprDataSource, backupImageSPRDataSource, localMetadata, remoteMetadata, contentResolver)
    }


    /**
     * FeeMarketing
     */
    @Provides
    fun provideFeeMarketingRepository(
        localFeeMarketingDataSource: LocalFeeMarketingDataSource,
        remoteFeeMarketingDataSource: RemoteFeeMarketingDataSource,
        backupFeeMarketingDataSource: BackupFeeMarketingDataSource,
        localMetadata: LocalMetadataDataSource,
        remoteMetadata: RemoteMetadataDataSource,
    ): FeeMarketingRepository {
        return FeeMarketingRepositoryImpl(localFeeMarketingDataSource, remoteFeeMarketingDataSource, backupFeeMarketingDataSource, localMetadata, remoteMetadata)
    }


    /**
     * Biaya Marketing
     */
    @Provides
    fun provideBiayaMarketingRepository(
        localBiayaMarketingDataSource: LocalBiayaMarketingDataSource,
        remoteBiayaMarketDataSource: RemoteBiayaMarketingDataSource,
        backupBiayaMarketingDataSource: BackupBiayaMarketingDataSource,
        localMetadata: LocalMetadataDataSource,
        remoteMetadata: RemoteMetadataDataSource,
    ): BiayaMarketingRepository {
        return BiayaMarketingRepositoryImpl(localBiayaMarketingDataSource, remoteBiayaMarketDataSource, backupBiayaMarketingDataSource, localMetadata, remoteMetadata)
    }


    /**
     * Kavling Catatan Pembayaran
     */
    @Provides
    fun provideKavlingCatatanPembayaranRepository(
        localKavlingCatatanPembayaranDataSource: LocalKavlingCatatanPembayaranDataSource,
        remoteKavlingCatatanPembayaranDataSource: RemoteKavlingCatatanPembayaranDataSource,
        backupCatatanPembayaranDataSource: BackupCatatanPembayaranDataSource,
    ): KavlingCatatanPembayaranRepository {
        return KavlingCatatanPembayaranRepositoryImpl(localKavlingCatatanPembayaranDataSource, remoteKavlingCatatanPembayaranDataSource, backupCatatanPembayaranDataSource)
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
        backupFotoPembayaranDataSource: BackupFotoPembayaranDataSource,
        localMetadataDataSource: LocalMetadataDataSource,
        remoteMetadataDataSource: RemoteMetadataDataSource,
    ): FotoPembayaranRepository {
        return FotoPembayaranRepositoryImpl(
            roomDataSource,
            deviceStorageDataSource,
            remoteFotoPembayaranDataSource,
            backupFotoPembayaranDataSource,
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
        backupBiayaLainDataSource: BackupBiayaLainDataSource,
        localMetadataDataSource: LocalMetadataDataSource,
        remoteMetadataDataSource: RemoteMetadataDataSource,
    ): BiayaLainRepository {
        return BiayaLainRepositoryImpl(
            localBiayaLainDataSource,
            remoteBiayaLainDataSource,
            backupBiayaLainDataSource,
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

}

/**
 * Refres to any deprecated Repositories.
 */
@Qualifier
annotation class Legacy

/**
 * Refers to any maintained Repositories.
 */
@Qualifier
annotation class Default