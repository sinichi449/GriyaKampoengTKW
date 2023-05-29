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
import net.bagusekasaputra.griyakampoengtkw.domain.repository.*
import java.io.File

@Module
@InstallIn(SingletonComponent::class)

object RepositoryModule {

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
    ): KavlingRepository {
        return KavlingRepositoryImpl(localKavlingDataSource, remoteKavlingDataSource, backupKavlingDataSource, localBlockDataSource)
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
    @Provides
    fun providePembayaranRepository(
        localPembayaranDataSource: LocalPembayaranDataSource,
        remotePembayaranSource: RemotePembayaranSource,
        backupPembayaranDataSource: BackupPembayaranDataSource,
        localMetadata: LocalMetadataDataSource,
        remoteMetadata: RemoteMetadataDataSource,
        cacheHelper: CacheHelper,
    ): PembayaranRepository {
        return PembayaranRepositoryImpl(
            localPembayaranDataSource,
            remotePembayaranSource,
            backupPembayaranDataSource,
            localMetadata,
            remoteMetadata,
            cacheHelper,
        )
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
        contentResolver: ContentResolver,
        cacheHelper: CacheHelper,
    ): ImageDataDiriRepository {
        return ImageDataDiriRepositoryImpl(
            localImageDataDiriDataSource,
            remoteImageDataDiriDataSource,
            backupImageDataDiriDataSource,
            localMetadataDataSource,
            remoteMetadataDataSource,
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
     * Catatan Pembayaran
     */
    @Provides
    fun provideCatatanPembayaranRepository(
        localCatatanPembayaranDataSource: LocalCatatanPembayaranDataSource,
        remoteCatatanPembayaranDataSource: RemoteCatatanPembayaranDataSource,
        backupCatatanPembayaranDataSource: BackupCatatanPembayaranDataSource,
    ): CatatanPembayaranRepository {
        return CatatanPembayaranRepositoryImpl(localCatatanPembayaranDataSource, remoteCatatanPembayaranDataSource, backupCatatanPembayaranDataSource)
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
            remoteMetadataDataSource
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
        @InternalDir internalFiles: File,
        backupBlokDataSource: BackupBlokDataSource,
        backupKavlingDataSource: BackupKavlingDataSource,
        backupPembayaranDataSource: BackupPembayaranDataSource,
        backupDataDiriDataSource: BackupDataDiriDataSource,
        backupHargaKavlingDataSource: BackupHargaKavlingDataSource,
        backupCatatanPembayaranDataSource: BackupCatatanPembayaranDataSource,
        backupBiayaMarketingDataSource: BackupBiayaMarketingDataSource,
        backupFeeMarketingDataSource: BackupFeeMarketingDataSource,
        backupBiayaLainDataSource: BackupBiayaLainDataSource,
        backupImageDataDiriDataSource: BackupImageDataDiriDataSource,
        backupFotoPembayaranDataSource: BackupFotoPembayaranDataSource,
        backupImageSPRDataSource: BackupImageSPRDataSource,
        backupRestoreDataSource: BackupRestoreDataSource,
        remoteBackupRestoreDataSource: RemoteBackupRestoreDataSource,
    ): BackupRestoreRepository {
        return BackupRestoreRepositoryImpl(
            internalFiles,
            backupBlokDataSource,
            backupKavlingDataSource,
            backupPembayaranDataSource,
            backupDataDiriDataSource,
            backupHargaKavlingDataSource,
            backupCatatanPembayaranDataSource,
            backupBiayaMarketingDataSource,
            backupFeeMarketingDataSource,
            backupBiayaLainDataSource,
            backupImageDataDiriDataSource,
            backupFotoPembayaranDataSource,
            backupImageSPRDataSource,
            backupRestoreDataSource,
            remoteBackupRestoreDataSource,
        )
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
     * Ambil Kuitansi
     */
    @Provides
    fun provideAmbilKuitansiRepository(
        localDataSource: LocalAmbilKuitansiDataSource,
        remoteDataSource: RemoteAmbilKuitansiDataSource,
        cacheHelper: CacheHelper,
    ): AmbilKuitansiRepository {
        return AmbilKuitansiRepositoryImpl(localDataSource, remoteDataSource, cacheHelper)
    }
}