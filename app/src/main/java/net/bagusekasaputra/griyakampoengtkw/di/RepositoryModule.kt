package net.bagusekasaputra.griyakampoengtkw.di

import android.content.ContentResolver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.*
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.*
import net.bagusekasaputra.griyakampoengtkw.data.repository.*
import net.bagusekasaputra.griyakampoengtkw.domain.repository.*

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
    ): BlockRepository {
        return BlockRepositoryImpl(localBlockDataSource, remoteBlockDataSource)
    }


    /**
     * Kavling
     */
    @Provides
    fun provideKavlingRepository(
        localKavlingDataSource: LocalKavlingDataSource,
        remoteKavlingDataSource: RemoteKavlingDataSource,
    ): KavlingRepository {
        return KavlingRepositoryImpl(localKavlingDataSource, remoteKavlingDataSource)
    }


    /**
     * Data Diri
     */
    @Provides
    fun provideDataDiriRepository(
        localDataDiriDataSource: LocalDataDiriDataSource,
        remoteDataDiriRepository: RemoteDataDiriRepository,
        remoteKavlingDataSource: RemoteKavlingDataSource,
        localMetadata: LocalMetadataDataSource,
        remoteMetadata: RemoteMetadataDataSource,
    ): DataDiriRepository {
        return DataDiriRepositoryImpl(
            localDataDiriDataSource,
            remoteDataDiriRepository,
            remoteKavlingDataSource,
            localMetadata,
            remoteMetadata
        )
    }


    /**
     * Pembayaran
     */
    @Provides
    fun providePembayaranRepository(
        localPembayaranDataSource: LocalPembayaranDataSource,
        remotePembayaranSource: RemotePembayaranSource,
        localMetadata: LocalMetadataDataSource,
        remoteMetadata: RemoteMetadataDataSource,
    ): PembayaranRepository {
        return PembayaranRepositoryImpl(localPembayaranDataSource, remotePembayaranSource, localMetadata, remoteMetadata)
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
        return HargaKavlingRepositoryImpl(localHargaKavlingDataSource, remoteHargaKavlingSource, localMetadata, remoteMetadata)
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
        contentResolver: ContentResolver,
    ): ImageDataDiriRepository {
        return ImageDataDiriRepositoryImpl(
            localImageDataDiri = localImageDataDiriDataSource,
            remoteImageDataDiri = remoteImageDataDiriDataSource,
            localMetadata = localMetadataDataSource,
            remoteMetadata = remoteMetadataDataSource,
            contentResolver = contentResolver,
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
     * Catatan Pembayaran
     */
    @Provides
    fun provideCatatanPembayaranRepository(
        localCatatanPembayaranDataSource: LocalCatatanPembayaranDataSource,
        remoteCatatanPembayaranDataSource: RemoteCatatanPembayaranDataSource
    ): CatatanPembayaranRepository {
        return CatatanPembayaranRepositoryImpl(localCatatanPembayaranDataSource, remoteCatatanPembayaranDataSource)
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
     * Rekap Uang Masuk
     */
    @Provides
    fun provideRekapUangMasukRepository(localRekap: LocalRekapUangMasukDataSource): RekapUangMasukRepository {
        return RekapUangMasukRepositoryImpl(localRekap)
    }
}