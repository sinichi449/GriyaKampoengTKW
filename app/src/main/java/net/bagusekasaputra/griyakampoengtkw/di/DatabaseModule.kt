package net.bagusekasaputra.griyakampoengtkw.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoengtkw.ConstsSharedPrefs
import net.bagusekasaputra.griyakampoengtkw.data.CacheHelper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote_backup.FirebaseNodes
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes.Companion.firebaseUrl
import javax.inject.Qualifier
import javax.inject.Singleton
import net.bagusekasaputra.griyakampoengtkw.data.remote_backup.FirebaseNodes as BackupNodes

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideMyRoomDatabase(
        @ApplicationContext appContext: Context,
        sharedPrefs: SharedPreferences
    ): MyRoomDatabase {
        val onDestructiveMigrationCallback = object : RoomDatabase.Callback() {
            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                super.onDestructiveMigration(db)

                sharedPrefs.edit()
                    .putBoolean(ConstsSharedPrefs.CACHE_UNINITIALIZED_OR_DESTROYED, true)
                    .apply()
            }
        }

        val tahapanReference = getTahapanReference(sharedPrefs)
        val backupName = sharedPrefs.getBackupName()

        // Set database file name's suffix to `tahapanReference` if `backupName` is not `null`.
        // Note that if `backupName` is not null, it means user has selected `DataMode.DATA_LAMA`.
        val dbName = if (backupName.isNullOrEmpty()) {
            tahapanReference
        } else {
            backupName.replace(" ", "_") // Remote whitespaces
        }
        return Room.databaseBuilder(
            appContext, MyRoomDatabase::class.java,
            "GKT_${dbName}.db"
        )
            .fallbackToDestructiveMigration()
            .addCallback(onDestructiveMigrationCallback)
            .build()
    }

    /**
     * Firebase Realtime-Database
     */
    @RootReference
    @Provides
    fun providesRootFirebaseDatabaseReference()
        = FirebaseDatabase.getInstance(firebaseUrl).reference

    @TahapanReference
    @Provides
    fun provideTahapanFirebaseDatabaseReference(sharedPrefs: SharedPreferences): DatabaseReference {
        val rootReference = FirebaseDatabase.getInstance(firebaseUrl).reference
        val tahapanReference = rootReference.child(getTahapanReference(sharedPrefs))

        // Get reference by whether the `SharedPreferences`' has `backupName` value.
        // If yes, then it should point to `backups/$backupName` reference.
        return sharedPrefs.getBackupName().run {
            if (this.isNullOrEmpty()) {
                tahapanReference
            } else {
                tahapanReference.child(FirebaseNodes.BACKUPS)
                    .child(this)
            }
        }
    }

    /**
     * Firebase Storage
     */
    @RootReference
    @Provides
    fun provideRootStorageReference() = FirebaseStorage.getInstance().reference

    @TahapanReference
    @Provides
    fun provideTahapanStorageReference(sharedPrefs: SharedPreferences): StorageReference {
        val rootReference = FirebaseStorage.getInstance().reference
        val tahapanReference = rootReference.child(getTahapanReference(sharedPrefs))

        // Get reference by whether the `SharedPreferences`' has `backupName` value.
        // If yes, then it should point to `backups/$backupName` reference.
        return sharedPrefs.getBackupName().run {
            if (this.isNullOrEmpty()) {
                tahapanReference
            } else {
                tahapanReference.child(FirebaseNodes.BACKUPS)
                    .child(this)
            }
        }
    }


    @Singleton
    @Provides
    fun provideCacheHelper(
        localMetadataDataSource: LocalMetadataDataSource,
        remoteMetadataDataSource: RemoteMetadataDataSource,
    ): CacheHelper {
        return CacheHelper(localMetadataDataSource, remoteMetadataDataSource)
    }

    private fun getTahapanReference(sharedPrefs: SharedPreferences): String {
        return sharedPrefs.getString(ConstsSharedPrefs.SELECTED_TAHAPAN, "TAHAP_1")!!
    }

    private fun SharedPreferences.getBackupName(): String? {
        return getString(BackupNodes.KEY_BACKUP_NAME, "")
    }
}

@Qualifier
annotation class RootReference

@Qualifier
annotation class TahapanReference