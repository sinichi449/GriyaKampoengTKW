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
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoengtkw.ConstsSharedPrefs
import net.bagusekasaputra.griyakampoengtkw.data.CacheHelper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.remote.FirebaseNodes
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes.Companion.firebaseUrl
import java.io.File

@Module
@InstallIn(ViewModelComponent::class)
object PersistentModules {

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
        val nodeTypeReference = sharedPrefs.getString(ConstsSharedPrefs.NODE_TYPE, ConstsSharedPrefs.NODE_STANDARD)
        val backupName = sharedPrefs.getBackupName()

        // Set database file name's suffix to `tahapanReference` if `backupName` is not `null`.
        // Note that if `backupName` is not null, it means user has selected `DataMode.DATA_LAMA`.
        val dbName = if (backupName.isNullOrEmpty()) {
            "${tahapanReference}_${nodeTypeReference}"
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
    @TahapanReference
    @Provides
    fun provideTahapanFirebaseDatabaseReference(sharedPrefs: SharedPreferences): DatabaseReference {
        val rootReference = FirebaseDatabase.getInstance(firebaseUrl).reference
        // Get NODE_TYPE reference
        val nodeType = sharedPrefs.getString(ConstsSharedPrefs.NODE_TYPE, ConstsSharedPrefs.NODE_STANDARD)
        val tahapanReference = if (nodeType == ConstsSharedPrefs.NODE_PEMBATALAN) {
                rootReference.child(getTahapanReference(sharedPrefs))
                    .child("pembatalan")
            } else {
                rootReference.child(getTahapanReference(sharedPrefs))
            }

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

    @TahapanReference
    @Provides
    fun provideTahapanStorageReference(sharedPrefs: SharedPreferences): StorageReference {
        val rootReference = FirebaseStorage.getInstance().reference
        val nodeType = sharedPrefs.getString(ConstsSharedPrefs.NODE_TYPE, ConstsSharedPrefs.NODE_STANDARD)
        val tahapanReference = if (nodeType == ConstsSharedPrefs.NODE_PEMBATALAN) {
                rootReference.child(getTahapanReference(sharedPrefs))
                    .child("pembatalan_images")
            } else {
                rootReference.child(getTahapanReference(sharedPrefs))
            }

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
        return getString(ConstsSharedPrefs.BACKUP_NAME, "")
    }



    /**
     * Device Storage
     */
    @InternalDir
    @Provides
    fun provideInternalFilesDir(@ApplicationContext ctx: Context): File {
        return ctx.filesDir
    }

    @ExternalDir
    @Provides
    fun provideExternalFilesDir(@ApplicationContext context: Context, sharedPrefs: SharedPreferences): File? {
        val root = context.getExternalFilesDir(null)
        val tahapan = getTahapanReference(sharedPrefs)
        val nodeType = sharedPrefs.getString(ConstsSharedPrefs.NODE_TYPE, ConstsSharedPrefs.NODE_STANDARD)
        val backupName = sharedPrefs.getBackupName()
        val fileWithTahapan = File(root, "${tahapan}_${nodeType}")

        val resultFile: File = if (!backupName.isNullOrEmpty()) {
            File(fileWithTahapan, "${FirebaseNodes.BACKUPS}/$backupName")
        } else {
            fileWithTahapan
        }

        if (!resultFile.exists()) resultFile.mkdirs()

        return resultFile
    }
}