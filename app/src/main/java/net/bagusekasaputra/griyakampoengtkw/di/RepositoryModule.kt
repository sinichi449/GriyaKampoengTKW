package net.bagusekasaputra.griyakampoengtkw.di

import android.content.ContentResolver
import com.google.firebase.database.DatabaseReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.bagusekasaputra.griyakampoengtkw.data.repository.*
import net.bagusekasaputra.griyakampoengtkw.data.source.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoengtkw.data.source.local.imageDataDiri.LocalImageDataDiriSource
import net.bagusekasaputra.griyakampoengtkw.data.source.local.imageDataDiri.room.RoomLocalImageDataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.data.source.local.kavling.LocalKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.data.source.local.kavling.room.RoomLocalKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.appupdate.FirebaseAppUpdateSource
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.appupdate.RemoteAppUpdateSource
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.block.FirebaseBlockRepository
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.block.RemoteBlockRepository
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.datadiri.FirebaseDataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.datadiri.RemoteDataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.hargakavling.FirebaseHargaKavlingSource
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.hargakavling.RemoteHargaKavlingSource
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.kavling.FirebaseKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.kavling.RemoteKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.pembayaran.FirebasePembayaranSource
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.pembayaran.RemotePembayaranSource
import net.bagusekasaputra.griyakampoengtkw.domain.repository.*
import java.io.File

@Module
@InstallIn(SingletonComponent::class)

object RepositoryModule {

    // Block Repository
    @Provides
    fun provideBlockRepository(remoteBlockRepository: RemoteBlockRepository): BlockRepository {
        return BlockRepositoryImpl(remoteBlockRepository)
    }

    @Provides
    fun provideRemoteBlockRepository(databaseReference: DatabaseReference): RemoteBlockRepository {
        return FirebaseBlockRepository(databaseReference)
    }

    // Kavling Repository
    @Provides
    fun provideKavlingRepository(
        localKavlingRepository: LocalKavlingRepository,
        remoteKavlingRepository: RemoteKavlingRepository,
    ): KavlingRepository {
        return KavlingRepositoryImpl(localKavlingRepository, remoteKavlingRepository)
    }

    @Provides
    fun provideLocalKavlingRepository(myRoomDatabase: MyRoomDatabase): LocalKavlingRepository {
        return RoomLocalKavlingRepository(myRoomDatabase)
    }

    @Provides
    fun provideRemoteKavlingRepository(databaseReference: DatabaseReference): RemoteKavlingRepository {
        return FirebaseKavlingRepository(databaseReference)
    }

    // Data Diri Repository
    @Provides
    fun provideDataDiriRepository(remoteDataDiriRepository: RemoteDataDiriRepository): DataDiriRepository {
        return DataDiriRepositoryImpl(remoteDataDiriRepository)
    }

    @Provides
    fun provideRemoteDataDiriRepository(databaseReference: DatabaseReference): RemoteDataDiriRepository {
        return FirebaseDataDiriRepository(databaseReference)
    }

    // Pembayaran Repository
    @Provides
    fun providePembayaranRepository(
        remotePembayaranSource: RemotePembayaranSource,
    ): PembayaranRepository {
        return PembayaranRepositoryImpl(remotePembayaranSource)
    }

    @Provides
    fun provideRemotePembayaranSource(databaseReference: DatabaseReference): RemotePembayaranSource {
        return FirebasePembayaranSource(databaseReference)
    }

    // Harga Kavling Repository
    @Provides
    fun provideHargaKavlingRepository(remoteHargaKavlingSource: RemoteHargaKavlingSource): HargaKavlingRepository {
        return HargaKavlingRepositoryImpl(remoteHargaKavlingSource)
    }

    @Provides
    fun provideRemoteHargaKavlingSource(databaseReference: DatabaseReference): RemoteHargaKavlingSource {
        return FirebaseHargaKavlingSource(databaseReference)
    }

    // App Update Repository
    @Provides
    fun provideAppUpdateRepository(remoteAppUpdateSource: RemoteAppUpdateSource): AppUpdateRepository {
        return AppUpdateRepositoryImpl(remoteAppUpdateSource)
    }

    @Provides
    fun provideRemoteAppUpdateSource(databaseReference: DatabaseReference): RemoteAppUpdateSource {
        return FirebaseAppUpdateSource(databaseReference)
    }

    // Image Data Diri
    @Provides
    fun provideImageDataDiriRepository(
        localImageDataDiriSource: LocalImageDataDiriSource,
        contentResolver: ContentResolver,
        externalFileDir: File?,
    ): ImageDataDiriRepository {
        return ImageDataDiriRepositoryImpl(localImageDataDiriSource, contentResolver, externalFileDir)
    }

    @Provides
    fun provideLocalImageDataDiriSource(roomDatabase: MyRoomDatabase): LocalImageDataDiriSource {
        return RoomLocalImageDataDiriRepository(roomDatabase)
    }
}