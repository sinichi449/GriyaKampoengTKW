@file:Suppress("DEPRECATION")

package net.bagusekasaputra.griyakampoengtkw.di

import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.ambilKuitansi.indenBooking.RoomIndenBookingAmbilKuitansiDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.ambilKuitansi.standard.RoomStandardAmbilKuitansiDataSource
import net.bagusekasaputra.griyakampoeng.tkw.data.local.backupRestore.RoomBackupRestoreDataSource
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
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup.*
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.*
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.*
import net.bagusekasaputra.griyakampoengtkw.data.remote.ambilKuitansi.FirebaseIndenBookingAmbilKuitansiDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.ambilKuitansi.FirebaseStandardAmbilKuitansiDataSource
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
import net.bagusekasaputra.griyakampoengtkw.data.remote.pembayaran.FirebasePembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.pembayaran.RemotePembayaranIndenBookingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.promotion.FirebasePromotionDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.statusPembayaran.FirebaseStatusPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.tahapan.FirebaseTahapanDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote_backup.BackupFirebaseBackupRestoreDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote_backup.BackupFirebaseBiayaLainDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote_backup.BackupFirebaseBiayaMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote_backup.BackupFirebaseCatatanPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote_backup.BackupFirebaseDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote_backup.BackupFirebaseFeeMarketingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote_backup.BackupFirebaseFotoPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote_backup.BackupFirebaseHargaKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote_backup.BackupFirebaseImageDataDiriDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote_backup.BackupFirebaseImageSPRDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote_backup.BackupFirebaseKavlingDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote_backup.BackupFirebasePembayaranDataSource
import java.io.File
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    /**
     * Tahapan
     */
    @Provides
    fun provideRemoteTahapanDataSource(@RootReference rootDatabaseReference: DatabaseReference): RemoteTahapanDataSource {
        return FirebaseTahapanDataSource(rootDatabaseReference)
    }

    /**
     * Metadata
     */
    @Provides
    fun provideLocalMetadataDataSource(roomDatabase: MyRoomDatabase): LocalMetadataDataSource {
        return RoomMetadataDataSource(roomDatabase)
    }

    @Provides
    fun provideRemoteMetadataDataSource(@TahapanReference databaseReference: DatabaseReference): RemoteMetadataDataSource {
        return FirebaseMetadataDataSource(databaseReference)
    }

    /**
     * App Update
     */
    @Provides
    fun provideRemoteAppUpdateSource(
        @RootReference databaseReference: DatabaseReference
    ): RemoteAppUpdateSource {
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
    fun provideRemoteBlockDataSource(@TahapanReference databaseReference: DatabaseReference): RemoteBlockDataSource {
        return net.bagusekasaputra.griyakampoengtkw.data.remote.block.FirebaseBlockDataSource(
            databaseReference
        )
    }


    /**
     * Kavling
     */
    @Provides
    fun provideLocalKavlingDataSource(myRoomDatabase: MyRoomDatabase): LocalKavlingDataSource {
        return RoomKavlingDataSource(myRoomDatabase)
    }

    @Provides
    fun provideRemoteKavlingDataSource(@TahapanReference databaseReference: DatabaseReference): RemoteKavlingDataSource {
        return FirebaseKavlingDataSource(
            databaseReference
        )
    }

    @Provides
    fun provideBackupKavlingDataSource(): BackupKavlingDataSource {
        return BackupFirebaseKavlingDataSource()
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
    fun provideRemoteDataDiriRepository(@TahapanReference databaseReference: DatabaseReference): RemoteDataDiriDataSource {
        return net.bagusekasaputra.griyakampoengtkw.data.remote.datadiri.FirebaseDataDiriDataSource(
            databaseReference
        )
    }

    @Provides
    fun provideBackupDataDiriDataSource(): BackupDataDiriDataSource {
        return BackupFirebaseDataDiriDataSource()
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
        @TahapanReference storageReference: StorageReference,
        @ExternalDir externalFilesDir: File?,
        imageIndenBooking: RemoteFotoIdentitasIndenBookingDataSource,
    ): RemoteImageDataDiriDataSource {
        return StorageImageDataDiriDataSource(storageReference, externalFilesDir, imageIndenBooking)
    }

    @Provides
    fun provideBackupImageDataDiriDataSource(): BackupImageDataDiriDataSource {
        return BackupFirebaseImageDataDiriDataSource()
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
    fun provideRemoteImageSprDataSource(@TahapanReference storageReference: StorageReference, @ExternalDir externalFilesDir: File?): RemoteImageSprDataSource {
        return StorageImageSprDataSource(storageReference, externalFilesDir)
    }

    @Provides
    fun provideBackupImageSPRDataSource(): BackupImageSPRDataSource {
        return BackupFirebaseImageSPRDataSource()
    }


    /**
     * Fee Marketing
     */
    @Provides
    fun provideRemoteFeeMarketingDataSource(@TahapanReference databaseReference: DatabaseReference): RemoteFeeMarketingDataSource {
        return net.bagusekasaputra.griyakampoengtkw.data.remote.feeMarketing.FirebaseFeeMarketingDataSource(
            databaseReference
        )
    }

    @Provides
    fun provideLocalFeeMarketingDataSource(roomDatabase: MyRoomDatabase): LocalFeeMarketingDataSource {
        return RoomFeeMarketingDataSource(roomDatabase)
    }

    @Provides
    fun provideBackupFeeMarketingDataSource(): BackupFeeMarketingDataSource {
        return BackupFirebaseFeeMarketingDataSource()
    }


    /**
     * Biaya Marketing
     */
    @Provides
    fun provideRemoteBiayaMarketingDataSource(@TahapanReference databaseReference: DatabaseReference): RemoteBiayaMarketingDataSource {
        return net.bagusekasaputra.griyakampoengtkw.data.remote.biayaMarketing.FirebaseBiayaMarketingDataSource(
            databaseReference
        )
    }

    @Provides
    fun provideLocalBiayaMarketingDataSource(roomDatabase: MyRoomDatabase): LocalBiayaMarketingDataSource {
        return RoomBiayaMarketingDataSource(roomDatabase)
    }

    @Provides
    fun provideBackupBiayaMarketingDataSource(): BackupBiayaMarketingDataSource {
        return BackupFirebaseBiayaMarketingDataSource()
    }


    /**
     * Kavling Catatan Pembayaran
     */
    @Provides
    fun provideRemoteKavlingCatatanPembayaranDataSource(@TahapanReference databaseReference: DatabaseReference): RemoteKavlingCatatanPembayaranDataSource {
        return net.bagusekasaputra.griyakampoengtkw.data.remote.catatanPembayaran.FirebaseKavlingCatatanPembayaranDataSource(
            databaseReference
        )
    }

    @Provides
    fun provideLocalKavlingCatatanPembayaranDataSource(roomDatabase: MyRoomDatabase): LocalKavlingCatatanPembayaranDataSource {
        return RoomKavlingCatatanPembayaranDataSource(roomDatabase)
    }

    @Provides
    fun provideBackupCatatanPembayaranDataSource(): BackupCatatanPembayaranDataSource {
        return BackupFirebaseCatatanPembayaranDataSource()
    }

    /**
     * Inden Booking Catatan Pembayaran
     */
    @Provides
    fun provideLocalIndenBookingCatatanPembayaranDataSource(myRoomDatabase: MyRoomDatabase): LocalIndenBookingCatatanPembayaranDataSource {
        return RoomIndenBookingCatatanPembayaranDataSource(myRoomDatabase)
    }

    @Provides
    fun provideRemoteIndenBookingCatatanPembayaranDataSource(@TahapanReference databaseReference: DatabaseReference): RemoteIndenBookingCatatanPembayaranDataSource {
        return FirebaseIndenBookingCatatanPembayaranDataSource(databaseReference)
    }


    /**
    * Pembayaran
     */
    @Provides
    fun provideRemotePembayaranSource(
        @TahapanReference databaseReference: DatabaseReference,
        pembayaranIndenBookingDataSource: RemotePembayaranIndenBookingDataSource
    ): RemotePembayaranDataSource {
        return FirebasePembayaranDataSource(databaseReference, pembayaranIndenBookingDataSource)
    }

    @Provides
    fun provideLocalPembayaranDataSource(
        roomDatabase: MyRoomDatabase,
        pembayaranIndenBookingDataSource: LocalPembayaranIndenBookingDataSource
    ): LocalPembayaranDataSource {
        return RoomPembayaranLocalDataSource(roomDatabase, pembayaranIndenBookingDataSource)
    }

    @Provides
    fun provideBackupPembayaranDataSource(): BackupPembayaranDataSource {
        return BackupFirebasePembayaranDataSource()
    }


    /**
     * Harga Kavling
     */
    @Provides
    fun provideRemoteHargaKavlingSource(@TahapanReference databaseReference: DatabaseReference): RemoteHargaKavlingSource {
        return net.bagusekasaputra.griyakampoengtkw.data.remote.hargakavling.FirebaseHargaKavlingSource(
            databaseReference
        )
    }

    @Provides
    fun provideLocalHargaKavlingDataSource(roomDatabase: MyRoomDatabase): LocalHargaKavlingDataSource {
        return RoomHargaKavlingDataSource(roomDatabase)
    }

    @Provides
    fun provideBackupHargaKavlingDataSource(): BackupHargaKavlingDataSource {
        return BackupFirebaseHargaKavlingDataSource()
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
    fun provideRemoteFotoPembayaranDataSource(@TahapanReference storageReference: StorageReference, @ExternalDir externalFilesDir: File?): RemoteFotoPembayaranDataSource {
        return StorageFotoPembayaranDataSource(storageReference, externalFilesDir)
    }

    @Provides
    fun provideBackupFotoPembayaranDataSource(): BackupFotoPembayaranDataSource {
        return BackupFirebaseFotoPembayaranDataSource()
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
    fun provideRemoteBiayaLainDataSource(@TahapanReference databaseReference: DatabaseReference): RemoteBiayaLainDataSource {
        return FirebaseBiayaLainDataSource(databaseReference)
    }

    @Provides
    fun provideBackupBiayaLainDataSource(): BackupBiayaLainDataSource {
        return BackupFirebaseBiayaLainDataSource()
    }


    /**
     * Backup Restore
     */
    @Provides
    fun provideBackupRestoreDataSource(): BackupRestoreDataSource {
        return BackupFirebaseBackupRestoreDataSource()
    }

    @Provides
    fun provideLocalBackupRestoreDataSource(roomDatabase: MyRoomDatabase): LocalBackupRestoreDataSource {
        return RoomBackupRestoreDataSource(roomDatabase)
    }

    @Provides
    fun provideRemoteBackupRestoreDataSource(@TahapanReference databaseReference: DatabaseReference): RemoteBackupRestoreDataSource {
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
    fun provideRemoteBaselinePembayaranDataSource(@TahapanReference databaseReference: DatabaseReference): RemoteBaselinePembayaranDataSource {
        return FirebaseBaselinePembayaranDataSource(databaseReference)
    }


    /**
     * Inden Booking
     */
    @Provides
    fun provideRemoteIndenBookingDataSource(@TahapanReference databaseReference: DatabaseReference): RemoteIndenBookingDataSource {
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
    fun provideRemotePembayaranIndenBookingDataSource(@TahapanReference databaseReference: DatabaseReference): RemotePembayaranIndenBookingDataSource {
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
        @TahapanReference storageReference: StorageReference,
        @ExternalDir externalFilesDir: File?,
    ): RemoteFotoIdentitasIndenBookingDataSource {
        return FirebaseFotoIdentitasIndenBookingDataSource(storageReference, externalFilesDir)
    }


    /**
     * Database User
     */
    @Provides
    fun provideRemoteDatabaseUserDataSource(@TahapanReference databaseReference: DatabaseReference): RemoteDatabaseUserDataSource {
        return FirebaseDatabaseUserDataSource(databaseReference)
    }


    /**
     * Status Pembayaran
     */
    @Provides
    fun provideRemoteStatusPembayaranDataSource(@TahapanReference databaseReference: DatabaseReference): RemoteStatusPembayaranDataSource {
        return FirebaseStatusPembayaranDataSource(databaseReference)
    }
    /**
     * Promotion
     */
    @Provides
    fun provideRemotePromotionDataSource(
        @RootReference databaseReference: DatabaseReference
    ): RemotePromotionDataSource {
        return FirebasePromotionDataSource(databaseReference)
    }

    /**
     * Standard Ambil Kuitansi
     */
    @Provides
    fun provideLocalStandardAmbilKuitansiDataSource(myRoomDatabase: MyRoomDatabase): LocalStandardAmbilKuitansiDataSource {
        return RoomStandardAmbilKuitansiDataSource(myRoomDatabase)
    }

    @Provides
    fun provideRemoteStandardAmbilKuitansiDataSource(@TahapanReference databaseReference: DatabaseReference): RemoteStandardAmbilKuitansiDataSource {
        return FirebaseStandardAmbilKuitansiDataSource(databaseReference)
    }

    /**
     * Inden Booking Ambil Kuitansi
     */
    @Provides
    fun provideLocalIndenBookinAmbilKuitansiDataSource(myRoomDatabase: MyRoomDatabase): LocalIndenBookingAmbilKuitansiDataSource {
        return RoomIndenBookingAmbilKuitansiDataSource(myRoomDatabase)
    }

    @Provides
    fun provideRemoteIndenBookingAmbilKuitansiDataSource(@TahapanReference databaseReference: DatabaseReference): RemoteIndenBookingAmbilKuitansiDataSource {
        return FirebaseIndenBookingAmbilKuitansiDataSource(databaseReference)
    }


    /**
     * Harga Rumah Inden Booking
     */
    @Provides
    fun provideLocalHargaRumahIndenBookingDataSource(myRoomDatabase: MyRoomDatabase): LocalHargaRumahIndenBookingDataSource {
        return RoomHargaRumahDataSource(myRoomDatabase)
    }

    @Provides
    fun provideRemoteHargaRumahIndenBookingDataSource(@TahapanReference databaseReference: DatabaseReference): RemoteHargaRumahIndenBookingDataSource {
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
    fun provideRemoteFotoPembayaranIndenBookingDataSource(@TahapanReference storageReference: StorageReference): RemoteFotoPembayaranIndenBookingDataSource {
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
    fun provideRemoteImageDataDiriIndenBookingDataSource(@TahapanReference storageReference: StorageReference): RemoteImageDataDiriIndenBookingDataSource {
        return FirebaseImageDataDiriIndenBookingDataSource(storageReference)
    }

}