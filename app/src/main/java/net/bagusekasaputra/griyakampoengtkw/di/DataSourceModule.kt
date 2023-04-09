package net.bagusekasaputra.griyakampoengtkw.di

import android.content.SharedPreferences
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.biayaLain.RoomBiayaLainDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.biayaMarketing.RoomBiayaMarketingDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.block.RoomBlockDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.catatanPembayaran.RoomCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.datadiri.RoomDataDiriDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.feeMarketing.RoomFeeMarketingDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.fotoKuitansi.RoomFotoKuitansiDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.fotoPembayaran.LocalFotoPembayaranDataSourceImpl
import net.bagusekasaputra.griyakampoeng.tkw.data.local.fotoPembayaran.device.DeviceFotoPembayaranDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.hargaKavling.RoomHargaKavlingDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.imageDataDiri.LocalImageDataDiriDataSourceImpl
import net.bagusekasaputra.griyakampoeng.tkw.data.local.imageSpr.LocalImageSprDataSourceImpl
import net.bagusekasaputra.griyakampoeng.tkw.data.local.kavling.RoomKavlingDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.metadata.RoomMetadataDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.pembayaran.RoomPembayaranLocalDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.pengingat.RoomPengingatDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.rekap.RoomRekapUangMasukLocalDataSource
import net.bagusekasaputra.griyakampoengtkw.data.backup.blok.BackupBlokDataSourceImpl
import net.bagusekasaputra.griyakampoengtkw.data.backup.kavling.BackupKavlingDataSourceImpl
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupBlokDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.BackupKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.*
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.*
import net.bagusekasaputra.griyakampoengtkw.data.remote.biayaLain.FirebaseBiayaLainDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.fotoPembayaran.StorageFotoPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.imageDataDiri.StorageImageDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.imageSpr.StorageImageSprDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.kavling.FirebaseKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.metadata.FirebaseMetadataDataSource
import java.io.File
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    /**
     * Metadata
     */
    @Provides
    fun provideLocalMetadataDataSource(roomDatabase: MyRoomDatabase): LocalMetadataDataSource {
        return RoomMetadataDataSource(roomDatabase)
    }

    @Provides
    fun provideRemoteMetadataDataSource(databaseReference: DatabaseReference): RemoteMetadataDataSource {
        return FirebaseMetadataDataSource(databaseReference)
    }

    /**
     * App Update
     */
    @Provides
    fun provideRemoteAppUpdateSource(databaseReference: DatabaseReference): RemoteAppUpdateSource {
        return net.bagusekasaputra.griyakampoengtkw.data.remote.appupdate.FirebaseAppUpdateSource(
            databaseReference
        )
    }


    /**
     * Blocks
     */
    @Provides
    fun provideLocalBlockDataSource(roomDatabase: MyRoomDatabase): LocalBlockDataSource {
        return RoomBlockDataSource(roomDatabase)
    }

    @Provides
    fun provideRemoteBlockDataSource(databaseReference: DatabaseReference): RemoteBlockDataSource {
        return net.bagusekasaputra.griyakampoengtkw.data.remote.block.FirebaseBlockDataSource(
            databaseReference
        )
    }

    @Provides
    fun provideBackupBlokDataSource(sharedPreferences: SharedPreferences): BackupBlokDataSource {
        return BackupBlokDataSourceImpl(sharedPreferences)
    }


    /**
     * Kavling
     */
    @Provides
    fun provideLocalKavlingDataSource(myRoomDatabase: MyRoomDatabase): LocalKavlingDataSource {
        return RoomKavlingDataSource(myRoomDatabase)
    }

    @Provides
    fun provideRemoteKavlingDataSource(databaseReference: DatabaseReference): RemoteKavlingDataSource {
        return FirebaseKavlingDataSource(
            databaseReference
        )
    }

    @Provides
    fun provideBackupKavlingDataSource(sharedPreferences: SharedPreferences): BackupKavlingDataSource {
        return BackupKavlingDataSourceImpl(sharedPreferences)
    }


    /**
     * Data Diri
     */
    @Provides
    fun provideLocalDataDiriRepository(roomDatabase: MyRoomDatabase): LocalDataDiriDataSource {
        return RoomDataDiriDataSource(roomDatabase)
    }

    @Provides
    fun provideRemoteDataDiriRepository(databaseReference: DatabaseReference): RemoteDataDiriRepository {
        return net.bagusekasaputra.griyakampoengtkw.data.remote.datadiri.FirebaseDataDiriRepository(
            databaseReference
        )
    }

    /**
     * Image Data Diri
     */
    @Provides
    fun provideLocalImageDataDiriSource(roomDatabase: MyRoomDatabase, @ExternalDir externalFilesDir: File?): LocalImageDataDiriDataSource {
        return LocalImageDataDiriDataSourceImpl(roomDatabase, externalFilesDir)
    }

    @Provides
    fun provideRemoteImageDataDiriSource(storageReference: StorageReference, @ExternalDir externalFilesDir: File?): RemoteImageDataDiriDataSource {
        return StorageImageDataDiriDataSource(storageReference, externalFilesDir)
    }


    /**
     * Foto Kuitansi
     */
    @Provides
    fun provideLocalFotoKuitansiRepository(roomDatabase: MyRoomDatabase): LocalFotoKuitansiDataSource {
        return RoomFotoKuitansiDataSource(roomDatabase)
    }


    /**
     * Image SPR
     */
    @Provides
    fun provideLocalImageSprDataSource(roomDatabase: MyRoomDatabase, @ExternalDir externalFilesDir: File?): LocalImageSprDataSource {
        return LocalImageSprDataSourceImpl(roomDatabase, externalFilesDir)
    }

    @Provides
    fun provideRemoteImageSprDataSource(storageReference: StorageReference, @ExternalDir externalFilesDir: File?): RemoteImageSprDataSource {
        return StorageImageSprDataSource(storageReference, externalFilesDir)
    }


    /**
     * Fee Marketing
     */
    @Provides
    fun provideRemoteFeeMarketingDataSource(databaseReference: DatabaseReference): RemoteFeeMarketingDataSource {
        return net.bagusekasaputra.griyakampoengtkw.data.remote.feeMarketing.FirebaseFeeMarketingDataSource(
            databaseReference
        )
    }

    @Provides
    fun provideLocalFeeMarketingDataSource(roomDatabase: MyRoomDatabase): LocalFeeMarketingDataSource {
        return RoomFeeMarketingDataSource(roomDatabase)
    }


    /**
     * Biaya Marketing
     */
    @Provides
    fun provideRemoteBiayaMarketingDataSource(databaseReference: DatabaseReference): RemoteBiayaMarketingDataSource {
        return net.bagusekasaputra.griyakampoengtkw.data.remote.biayaMarketing.FirebaseBiayaMarketingDataSource(
            databaseReference
        )
    }

    @Provides
    fun provideLocalBiayaMarketingDataSource(roomDatabase: MyRoomDatabase): LocalBiayaMarketingDataSource {
        return RoomBiayaMarketingDataSource(roomDatabase)
    }


    /**
     * Catatan Pembayaran
     */
    @Provides
    fun provideRemoteCatatanPembayaranDataSource(databaseReference: DatabaseReference): RemoteCatatanPembayaranDataSource {
        return net.bagusekasaputra.griyakampoengtkw.data.remote.catatanPembayaran.FirebaseCatatanPembayaranDataSource(
            databaseReference
        )
    }

    @Provides
    fun provideLocalCatatanPembayaranDataSource(roomDatabase: MyRoomDatabase): LocalCatatanPembayaranDataSource {
        return RoomCatatanPembayaranDataSource(roomDatabase)
    }


    /**
    * Pembayaran
     */
    @Provides
    fun provideRemotePembayaranSource(databaseReference: DatabaseReference): RemotePembayaranSource {
        return net.bagusekasaputra.griyakampoengtkw.data.remote.pembayaran.FirebasePembayaranSource(
            databaseReference
        )
    }

    @Provides
    fun provideLocalPembayaranDataSource(roomDatabase: MyRoomDatabase): LocalPembayaranDataSource {
        return RoomPembayaranLocalDataSource(roomDatabase)
    }


    /**
     * Harga Kavling
     */
    @Provides
    fun provideRemoteHargaKavlingSource(databaseReference: DatabaseReference): RemoteHargaKavlingSource {
        return net.bagusekasaputra.griyakampoengtkw.data.remote.hargakavling.FirebaseHargaKavlingSource(
            databaseReference
        )
    }

    @Provides
    fun provideLocalHargaKavlingDataSource(roomDatabase: MyRoomDatabase): LocalHargaKavlingDataSource {
        return RoomHargaKavlingDataSource(roomDatabase)
    }


    @Qualifier
    annotation class RoomDatabase

    @Qualifier
    annotation class DeviceStorage


    /**
     * Foto Pembayaran
     */
    @Provides
    @RoomDatabase
    fun provideLocalFotoPembayaranDataSource(roomDatabase: MyRoomDatabase, @ExternalDir externalFilesDir: File?): LocalFotoPembayaranDataSource {
        return LocalFotoPembayaranDataSourceImpl(roomDatabase, externalFilesDir)
    }

    @Provides
    @DeviceStorage
    fun provideDeviceFotoPembayaranDataSource(@ExternalDir externalFilesDir: File?): LocalFotoPembayaranDataSource {
        return DeviceFotoPembayaranDataSource(externalFilesDir)
    }

    @Provides
    fun provideRemoteFotoPembayaranDataSource(storageReference: StorageReference, @ExternalDir externalFilesDir: File?): RemoteFotoPembayaranDataSource {
        return StorageFotoPembayaranDataSource(storageReference, externalFilesDir)
    }


    /**
     * Pengingat
     */
    @Provides
    fun provideLocalPengingatDataSource(roomDatabase: MyRoomDatabase): LocalPengingatDataSource {
        return RoomPengingatDataSource(roomDatabase)
    }

    /**
     * Biaya Lain
     */
    @Provides
    fun provideLocalBiayaLainDataSource(roomDatabase: MyRoomDatabase): LocalBiayaLainDataSource {
        return RoomBiayaLainDataSource(roomDatabase)
    }

    @Provides
    fun provideRemoteBiayaLainDataSource(databaseReference: DatabaseReference): RemoteBiayaLainDataSource {
        return FirebaseBiayaLainDataSource(databaseReference)
    }

    /**
     * Rekap Uang Masuk
     */
    @Provides
    fun provideLocalRekapUangMasukDataSource(roomDatabase: MyRoomDatabase): LocalRekapUangMasukDataSource {
        return RoomRekapUangMasukLocalDataSource(roomDatabase)
    }
}