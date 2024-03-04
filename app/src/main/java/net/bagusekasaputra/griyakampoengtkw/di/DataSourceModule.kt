@file:Suppress("DEPRECATION")

package net.bagusekasaputra.griyakampoengtkw.di

import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.sources.*
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.*
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.*
import net.bagusekasaputra.griyakampoengtkw.data.remote.sources.*
import java.io.File
import javax.inject.Qualifier

@Module
@InstallIn(ViewModelComponent::class)
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
        return FirebaseAppUpdateSource(
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
        return FirebaseBlockDataSource(
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
        return FirebaseDataDiriDataSource(
            databaseReference
        )
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


    /**
     * Fee Marketing
     */
    @Provides
    fun provideRemoteFeeMarketingDataSource(@TahapanReference databaseReference: DatabaseReference): RemoteFeeMarketingDataSource {
        return FirebaseFeeMarketingDataSource(
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
    fun provideRemoteBiayaMarketingDataSource(@TahapanReference databaseReference: DatabaseReference): RemoteBiayaMarketingDataSource {
        return FirebaseBiayaMarketingDataSource(
            databaseReference
        )
    }

    @Provides
    fun provideLocalBiayaMarketingDataSource(roomDatabase: MyRoomDatabase): LocalBiayaMarketingDataSource {
        return RoomBiayaMarketingDataSource(roomDatabase)
    }


    /**
     * Kavling Catatan Pembayaran
     */
    @Provides
    fun provideRemoteKavlingCatatanPembayaranDataSource(@TahapanReference databaseReference: DatabaseReference): RemoteKavlingCatatanPembayaranDataSource {
        return FirebaseKavlingCatatanPembayaranDataSource(
            databaseReference
        )
    }

    @Provides
    fun provideLocalKavlingCatatanPembayaranDataSource(roomDatabase: MyRoomDatabase): LocalKavlingCatatanPembayaranDataSource {
        return RoomKavlingCatatanPembayaranDataSource(roomDatabase)
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


    /**
     * Harga Kavling
     */
    @Provides
    fun provideRemoteHargaKavlingSource(@TahapanReference databaseReference: DatabaseReference): RemoteHargaKavlingSource {
        return FirebaseHargaKavlingSource(
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
    fun provideRemoteFotoPembayaranDataSource(@TahapanReference storageReference: StorageReference, @ExternalDir externalFilesDir: File?): RemoteFotoPembayaranDataSource {
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
    fun provideRemoteBiayaLainDataSource(@TahapanReference databaseReference: DatabaseReference): RemoteBiayaLainDataSource {
        return FirebaseBiayaLainDataSource(databaseReference)
    }


    /**
     * Backup Restore
     */

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

    /**
     * Pengembalian Pembayaran
     */
    @Provides
    fun provideLocalPengembalianDataSource(myRoomDatabase: MyRoomDatabase): LocalPengembalianDataSource {
        return RoomPengembalianDataSource(myRoomDatabase)
    }

    @Provides
    fun provideRemotePengembalianDataSource(
        @TahapanReference databaseReference: DatabaseReference,
        @TahapanReference storageReference: StorageReference,
    ): RemotePengembalianDataSource {
        return FirebasePengembalianDataSource(databaseReference, storageReference)
    }

    /**
     * Pembayaran Tambah Luasan
     */
    @Provides
    fun provideLocalPembayaranTambahLuasanDataSource(): LocalPembayaranTambahLuasanDataSource
        = RoomPembayaranTambahLuasanDataSource()

    @Provides
    fun provideRemotePembayaranTambahLuasanDataSource(
        @TahapanReference databaseReference: DatabaseReference,
    ): RemotePembayaranTambahLuasanDataSource
        = FirebasePembayaranTambahLuasanDataSource(databaseReference)

    /**
     * Foto Tambah Luasan
     */
    @Provides
    fun provideRemoteFotoTambahLuasanDataSource(
        @TahapanReference storageReference: StorageReference,
        @ExternalDir externalFilesDir: File?
    ): RemoteFotoTambahLuasanDataSource {
        return StorageFotoTambahLuasanDataSource(storageReference, externalFilesDir)
    }
}