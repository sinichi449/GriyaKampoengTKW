@file:Suppress("DEPRECATION")

package net.bagusekasaputra.griyakampoengtkw.di

import android.content.SharedPreferences
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.ambilKuitansi.RoomAmbilKuitansiDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.baselinePembayaran.RoomBaselinePembayaranLocalDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.biayaLain.RoomBiayaLainDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.biayaMarketing.RoomBiayaMarketingDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.block.RoomBlockDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.catatanPembayaran.indenBooking.RoomIndenBookingCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.catatanPembayaran.kavling.RoomKavlingCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.datadiri.RoomDataDiriDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.datadiri.RoomDataDiriIndenBookingDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.feeMarketing.RoomFeeMarketingDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.fotoKuitansi.RoomFotoKuitansiDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.fotoPembayaran.LocalFotoPembayaranDataSourceImpl
import net.bagusekasaputra.griyakampoeng.tkw.data.local.fotoPembayaran.device.DeviceFotoPembayaranDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.hargaKavling.RoomHargaKavlingDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.imageDataDiri.LocalFotoIdentitasDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.imageDataDiri.LocalImageDataDiriDataSourceImpl
import net.bagusekasaputra.griyakampoeng.tkw.data.local.imageSpr.LocalImageSprDataSourceImpl
import net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking.RoomIndenBookingDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking.dataDiri.RoomDataDiriIndenBookingDataSourceImpl
import net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking.fotoPembayaran.RoomFotoPembayaranIndenBookingDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking.hargaRumah.RoomHargaRumahDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking.imageDataDiri.RoomFotoIdentitasDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking.imageDataDiri.RoomImageDataDiriIndenBookingDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking.pembayaran.RoomPembayaranIndenBookingDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.kavling.RoomKavlingDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.metadata.RoomMetadataDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.pembayaran.LocalPembayaranIndenBookingDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.pembayaran.RoomPembayaranLocalDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.pengingat.RoomPengingatDataSource
import net.bagusekasaputra.griyakampoengtkw.data.backup.BackupRestoreDataSourceImpl
import net.bagusekasaputra.griyakampoengtkw.data.backup.biayaLain.BackupBiayaLainDataSourceImpl
import net.bagusekasaputra.griyakampoengtkw.data.backup.biayaMarketing.BackupBiayaMarketingDataSourceImpl
import net.bagusekasaputra.griyakampoengtkw.data.backup.blok.BackupBlokDataSourceImpl
import net.bagusekasaputra.griyakampoengtkw.data.backup.catatanPembayaran.BackupCatatanPembayaranDataSourceImpl
import net.bagusekasaputra.griyakampoengtkw.data.backup.dataDiri.BackupDataDiriDataSourceImpl
import net.bagusekasaputra.griyakampoengtkw.data.backup.feeMarketing.BackupFeeMarketingDataSourceImpl
import net.bagusekasaputra.griyakampoengtkw.data.backup.fotoPembayaran.BackupFotoPembayaranDataSourceImpl
import net.bagusekasaputra.griyakampoengtkw.data.backup.hargaKavling.BackupHargaKavlingDataSourceImpl
import net.bagusekasaputra.griyakampoengtkw.data.backup.imageDataDiri.BackupImageDataDiriDataSourceImpl
import net.bagusekasaputra.griyakampoengtkw.data.backup.imageSpr.BackupImageSPRDataSourceImpl
import net.bagusekasaputra.griyakampoengtkw.data.backup.kavling.BackupKavlingDataSourceImpl
import net.bagusekasaputra.griyakampoengtkw.data.backup.pembayaran.BackupPembayaranDataSourceImpl
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.*
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.*
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.*
import net.bagusekasaputra.griyakampoengtkw.data.remote.ambilKuitansi.FirebaseAmbilKuitansiDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.backupRestore.FirebaseBackupRestoreDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.baselinePembayaran.FirebaseBaselinePembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.biayaLain.FirebaseBiayaLainDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.catatanPembayaran.FirebaseIndenBookingCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.databaseUser.FirebaseDatabaseUserDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.fotoPembayaran.StorageFotoPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.imageDataDiri.RemoteFotoIdentitasIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.imageDataDiri.StorageImageDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.imageSpr.StorageImageSprDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.indenBooking.FirebaseIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.indenBooking.fotoPembayaran.FirebaseFotoPembayaranIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.indenBooking.hargaRumah.FirebaseHargaRumahDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.indenBooking.imageDataDiri.FirebaseFotoIdentitasIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.indenBooking.imageDataDiri.FirebaseImageDataDiriIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.indenBooking.pembayaran.FirebasePembayaranIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.kavling.FirebaseKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.metadata.FirebaseMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.pembayaran.FirebasePembayaranSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.pembayaran.RemotePembayaranIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.promotion.FirebasePromotionDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.statusPembayaran.FirebaseStatusPembayaranDataSource
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
    fun provideLocalDataDiriRepository(
        roomDatabase: MyRoomDatabase,
        dataDiriIndenBookingDataSource: RoomDataDiriIndenBookingDataSource,
    ): LocalDataDiriDataSource {
        return RoomDataDiriDataSource(roomDatabase, dataDiriIndenBookingDataSource)
    }

    @Provides
    fun provideRemoteDataDiriRepository(databaseReference: DatabaseReference): RemoteDataDiriDataSource {
        return net.bagusekasaputra.griyakampoengtkw.data.remote.datadiri.FirebaseDataDiriDataSource(
            databaseReference
        )
    }

    @Provides
    fun provideBackupDataDiriDataSource(sharedPreferences: SharedPreferences): BackupDataDiriDataSource {
        return BackupDataDiriDataSourceImpl(sharedPreferences)
    }

    /**
     * Image Data Diri
     */
    @Provides
    fun provideLocalImageDataDiriSource(
        roomDatabase: MyRoomDatabase,
        @ExternalDir externalFilesDir: File?,
        imageIndenBooking: LocalFotoIdentitasDataSource
    ): LocalImageDataDiriDataSource {
        return LocalImageDataDiriDataSourceImpl(roomDatabase, externalFilesDir, imageIndenBooking)
    }

    @Provides
    fun provideRemoteImageDataDiriSource(
        storageReference: StorageReference,
        @ExternalDir externalFilesDir: File?,
        imageIndenBooking: RemoteFotoIdentitasIndenBookingDataSource,
    ): RemoteImageDataDiriDataSource {
        return StorageImageDataDiriDataSource(storageReference, externalFilesDir, imageIndenBooking)
    }

    @Provides
    fun provideBackupImageDataDiriDataSource(sharedPreferences: SharedPreferences): BackupImageDataDiriDataSource {
        return BackupImageDataDiriDataSourceImpl(sharedPreferences)
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

    @Provides
    fun provideBackupImageSPRDataSource(sharedPreferences: SharedPreferences): BackupImageSPRDataSource {
        return BackupImageSPRDataSourceImpl(sharedPreferences)
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

    @Provides
    fun provideBackupFeeMarketingDataSource(sharedPreferences: SharedPreferences): BackupFeeMarketingDataSource {
        return BackupFeeMarketingDataSourceImpl(sharedPreferences)
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

    @Provides
    fun provideBackupBiayaMarketingDataSource(sharedPreferences: SharedPreferences): BackupBiayaMarketingDataSource {
        return BackupBiayaMarketingDataSourceImpl(sharedPreferences)
    }


    /**
     * Kavling Catatan Pembayaran
     */
    @Provides
    fun provideRemoteKavlingCatatanPembayaranDataSource(databaseReference: DatabaseReference): RemoteKavlingCatatanPembayaranDataSource {
        return net.bagusekasaputra.griyakampoengtkw.data.remote.catatanPembayaran.FirebaseKavlingCatatanPembayaranDataSource(
            databaseReference
        )
    }

    @Provides
    fun provideLocalKavlingCatatanPembayaranDataSource(roomDatabase: MyRoomDatabase): LocalKavlingCatatanPembayaranDataSource {
        return RoomKavlingCatatanPembayaranDataSource(roomDatabase)
    }

    @Provides
    fun provideBackupCatatanPembayaranDataSource(sharedPreferences: SharedPreferences): BackupCatatanPembayaranDataSource {
        return BackupCatatanPembayaranDataSourceImpl(sharedPreferences)
    }

    /**
     * Inden Booking Catatan Pembayaran
     */
    @Provides
    fun provideLocalIndenBookingCatatanPembayaranDataSource(myRoomDatabase: MyRoomDatabase): LocalIndenBookingCatatanPembayaranDataSource {
        return RoomIndenBookingCatatanPembayaranDataSource(myRoomDatabase)
    }

    @Provides
    fun provideRemoteIndenBookingCatatanPembayaranDataSource(databaseReference: DatabaseReference): RemoteIndenBookingCatatanPembayaranDataSource {
        return FirebaseIndenBookingCatatanPembayaranDataSource(databaseReference)
    }


    /**
    * Pembayaran
     */
    @Provides
    fun provideRemotePembayaranSource(
        databaseReference: DatabaseReference,
        pembayaranIndenBookingDataSource: RemotePembayaranIndenBookingDataSource
    ): RemotePembayaranSource {
        return FirebasePembayaranSource(databaseReference, pembayaranIndenBookingDataSource)
    }

    @Provides
    fun provideLocalPembayaranDataSource(
        roomDatabase: MyRoomDatabase,
        pembayaranIndenBookingDataSource: LocalPembayaranIndenBookingDataSource
    ): LocalPembayaranDataSource {
        return RoomPembayaranLocalDataSource(roomDatabase, pembayaranIndenBookingDataSource)
    }

    @Provides
    fun provideBackupPembayaranDataSource(sharedPreferences: SharedPreferences): BackupPembayaranDataSource {
        return BackupPembayaranDataSourceImpl(sharedPreferences)
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

    @Provides
    fun provideBackupHargaKavlingDataSource(sharedPreferences: SharedPreferences): BackupHargaKavlingDataSource {
        return BackupHargaKavlingDataSourceImpl(sharedPreferences)
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

    @Provides
    fun provideBackupFotoPembayaranDataSource(sharedPreferences: SharedPreferences): BackupFotoPembayaranDataSource {
        return BackupFotoPembayaranDataSourceImpl(sharedPreferences)
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

    @Provides
    fun provideBackupBiayaLainDataSource(sharedPreferences: SharedPreferences): BackupBiayaLainDataSource {
        return BackupBiayaLainDataSourceImpl(sharedPreferences)
    }


    /**
     * Backup Restore
     */
    @Provides
    fun provideBackupRestoreDataSource(): BackupRestoreDataSource {
        return BackupRestoreDataSourceImpl()
    }

    @Provides
    fun provideRemoteBackupRestoreDataSource(databaseReference: DatabaseReference): RemoteBackupRestoreDataSource {
        return FirebaseBackupRestoreDataSource(databaseReference)
    }


    /**
     * Baseline Pembayaran
     */
    @Provides
    fun provideLocalBaselinePembayaranDataSource(myRoomDatabase: MyRoomDatabase): LocalBaselinePembayaranDataSource {
        return RoomBaselinePembayaranLocalDataSource(myRoomDatabase)
    }

    @Provides
    fun provideRemoteBaselinePembayaranDataSource(databaseReference: DatabaseReference): RemoteBaselinePembayaranDataSource {
        return FirebaseBaselinePembayaranDataSource(databaseReference)
    }


    /**
     * Inden Booking
     */
    @Provides
    fun provideRemoteIndenBookingDataSource(databaseReference: DatabaseReference): RemoteIndenBookingDataSource {
        return FirebaseIndenBookingDataSource(databaseReference)
    }

    @Provides
    fun provideLocalIndenBookingDataSource(
        myRoomDatabase: MyRoomDatabase,
        @ExternalDir externalFilesDir: File?,
    ): LocalIndenBookingDataSource {
        return RoomIndenBookingDataSource(myRoomDatabase, externalFilesDir)
    }

    // Inden Booking - Data Diri
    @Provides
    fun provideRoomDataDiriIndenBookingDataSource(myRoomDatabase: MyRoomDatabase): RoomDataDiriIndenBookingDataSource {
        return RoomDataDiriIndenBookingDataSourceImpl(myRoomDatabase)
    }

    // Inden Booking - Pembayaran
    @Provides
    fun provideLocalPembayaranIndenBookingDataSource(myRoomDatabase: MyRoomDatabase): LocalPembayaranIndenBookingDataSource {
        return RoomPembayaranIndenBookingDataSource(myRoomDatabase)
    }

    @Provides
    fun provideRemotePembayaranIndenBookingDataSource(databaseReference: DatabaseReference): RemotePembayaranIndenBookingDataSource {
        return FirebasePembayaranIndenBookingDataSource(databaseReference)
    }

    // Inden Booking - Foto Identitas / Image Data Diri
    @Provides
    fun provideLocalFotoIdentitasIndenBookingDataSource(
        myRoomDatabase: MyRoomDatabase,
        @ExternalDir externalFilesDir: File?
    ): LocalFotoIdentitasDataSource {
        return RoomFotoIdentitasDataSource(myRoomDatabase, externalFilesDir)
    }

    @Provides
    fun providesRemoteFotoIdentitasIndenBookingDataSource(
        storageReference: StorageReference,
        @ExternalDir externalFilesDir: File?,
    ): RemoteFotoIdentitasIndenBookingDataSource {
        return FirebaseFotoIdentitasIndenBookingDataSource(storageReference, externalFilesDir)
    }


    /**
     * Database User
     */
    @Provides
    fun provideRemoteDatabaseUserDataSource(databaseReference: DatabaseReference): RemoteDatabaseUserDataSource {
        return FirebaseDatabaseUserDataSource(databaseReference)
    }


    /**
     * Status Pembayaran
     */
    @Provides
    fun provideRemoteStatusPembayaranDataSource(databaseReference: DatabaseReference): RemoteStatusPembayaranDataSource {
        return FirebaseStatusPembayaranDataSource(databaseReference)
    }
    /**
     * Promotion
     */
    @Provides
    fun provideRemotePromotionDataSource(databaseReference: DatabaseReference): RemotePromotionDataSource {
        return FirebasePromotionDataSource(databaseReference)
    }

    /**
     * Ambil Kuitansi
     */
    @Provides
    fun provideLocalAmbilKuitansiDataSource(myRoomDatabase: MyRoomDatabase): LocalAmbilKuitansiDataSource {
        return RoomAmbilKuitansiDataSource(myRoomDatabase)
    }

    @Provides
    fun provideRemoteAmbilKuitansiDataSource(databaseReference: DatabaseReference): RemoteAmbilKuitansiDataSource {
        return FirebaseAmbilKuitansiDataSource(databaseReference)
    }

    /**
     * Harga Rumah Inden Booking
     */
    @Provides
    fun provideLocalHargaRumahIndenBookingDataSource(myRoomDatabase: MyRoomDatabase): LocalHargaRumahIndenBookingDataSource {
        return RoomHargaRumahDataSource(myRoomDatabase)
    }

    @Provides
    fun provideRemoteHargaRumahIndenBookingDataSource(databaseReference: DatabaseReference): RemoteHargaRumahIndenBookingDataSource {
        return FirebaseHargaRumahDataSource(databaseReference)
    }

    /**
     * Foto Pembayaran Inden Booking
     */
    @Provides
    fun provideLocalFotoPembayaranIndenBookingDataSource(
        myRoomDatabase: MyRoomDatabase,
        @ExternalDir rootExternalDir: File?
    ): LocalFotoPembayaranIndenBookingDataSource {
        return RoomFotoPembayaranIndenBookingDataSource(myRoomDatabase, rootExternalDir)
    }

    @Provides
    fun provideRemoteFotoPembayaranIndenBookingDataSource(storageReference: StorageReference): RemoteFotoPembayaranIndenBookingDataSource {
        return FirebaseFotoPembayaranIndenBookingDataSource(storageReference)
    }

    /**
     * Image Data Diri Inden Booking
     */
    @Provides
    fun provideLocalImageDataDiriIndenBookingDataSource(
        myRoomDatabase: MyRoomDatabase,
        @ExternalDir externalFileDir: File?
    ): LocalImageDataDiriIndenBookingDataSource {
        return RoomImageDataDiriIndenBookingDataSource(myRoomDatabase, externalFileDir)
    }

    @Provides
    fun provideRemoteImageDataDiriIndenBookingDataSource(storageReference: StorageReference): RemoteImageDataDiriIndenBookingDataSource {
        return FirebaseImageDataDiriIndenBookingDataSource(storageReference)
    }

}