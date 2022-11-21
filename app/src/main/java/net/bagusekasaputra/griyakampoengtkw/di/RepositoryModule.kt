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
    ): DataDiriRepository {
        return DataDiriRepositoryImpl(localDataDiriDataSource, remoteDataDiriRepository, remoteKavlingDataSource)
    }


    /**
     * Pembayaran
     */
    @Provides
    fun providePembayaranRepository(
        localPembayaranDataSource: LocalPembayaranDataSource,
        remotePembayaranSource: RemotePembayaranSource,
    ): PembayaranRepository {
        return PembayaranRepositoryImpl(localPembayaranDataSource, remotePembayaranSource)
    }


    /**
     * Harga Kavling
     */
    @Provides
    fun provideHargaKavlingRepository(
        localHargaKavlingDataSource: LocalHargaKavlingDataSource,
        remoteHargaKavlingSource: RemoteHargaKavlingSource
    ): HargaKavlingRepository {
        return HargaKavlingRepositoryImpl(localHargaKavlingDataSource, remoteHargaKavlingSource)
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
        contentResolver: ContentResolver,
    ): ImageDataDiriRepository {
        return ImageDataDiriRepositoryImpl(localImageDataDiriDataSource, contentResolver)
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
        contentResolver: ContentResolver
    ): ImageSprRepository {
        return ImageSprRepositoryImpl(localImageSprDataSource, contentResolver)
    }



    // Fee Marketing
    @Provides
    fun provideFeeMarketingRepository(remoteFeeMarketingDataSource: RemoteFeeMarketingDataSource): FeeMarketingRepository {
        return FeeMarketingRepositoryImpl(remoteFeeMarketingDataSource)
    }


    // Biaya Marketing
    @Provides
    fun provideBiayaMarketingRepository(
        localBiayaMarketingDataSource: LocalBiayaMarketingDataSource,
        remoteBiayaMarketDataSource: RemoteBiayaMarketingDataSource,
    ): BiayaMarketingRepository {
        return BiayaMarketingRepositoryImpl(localBiayaMarketingDataSource, remoteBiayaMarketDataSource)
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


    // Foto Pembayaran
    @Provides
    fun provideFotoPembayaranRepository(
        @DataSourceModule.RoomDatabase roomDataSource: LocalFotoPembayaranDataSource,
        @DataSourceModule.DeviceStorage deviceStorageDataSource: LocalFotoPembayaranDataSource,
    ): FotoPembayaranRepository {
        return FotoPembayaranRepositoryImpl(roomDataSource, deviceStorageDataSource)
    }
}