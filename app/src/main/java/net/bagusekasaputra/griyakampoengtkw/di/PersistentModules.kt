package net.bagusekasaputra.griyakampoengtkw.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.firebase.FirebaseApp
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
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes
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

        // NEW: Get the selected branch. Default to empty or a code for the first branch.
        val branchPrefix = sharedPrefs.getString(ConstsSharedPrefs.SELECTED_BRANCH,
            ConstsSharedPrefs.BRANCH_GKT1) ?: ConstsSharedPrefs.BRANCH_GKT1

        // Set database file name's suffix to `tahapanReference` if `backupName` is not `null`.
        // Note that if `backupName` is not null, it means user has selected `DataMode.DATA_LAMA`.
        val dbName = if (backupName.isNullOrEmpty()) {
            // NEW: Add branchPrefix to the filename
            "$${branchPrefix}_${tahapanReference}_${nodeTypeReference}"
        } else {
            backupName.replace(" ", "_") // Remote whitespaces
        }
        return Room.databaseBuilder(
            appContext, MyRoomDatabase::class.java,
            "GKT_${dbName}.db"
        )
            .fallbackToDestructiveMigration() // Important: This wipes the DB if schema changes
            .addCallback(onDestructiveMigrationCallback)
            .build()
    }

    // --- HELPER FUNCTION: The "Branch Switcher" ---
    private fun getFirebaseApp(context: Context, branch: String): FirebaseApp {
        // If Branch 1, use the default app (configured by google-services.json)
        if (branch == ConstsSharedPrefs.BRANCH_GKT1) {
            return FirebaseApp.getInstance()
        }

        // If Branch 2, we must look for (or create) a secondary app
        val appName = "GKT2_SECONDARY_APP"

        return try {
            FirebaseApp.getInstance(appName)
        } catch (e: IllegalStateException) {
            // App not initialized yet, let's build it manually
            e.printStackTrace()
            val options = com.google.firebase.FirebaseOptions.Builder()
                .setApiKey(GriyaNodes.GKT2_API_KEY)
                .setApplicationId(GriyaNodes.GKT2_APP_ID)
                .setProjectId(GriyaNodes.GKT2_PROJECT_ID)
                .setDatabaseUrl(GriyaNodes.FIREBASE_RDB_GKT2)
                .setStorageBucket(GriyaNodes.FIREBASE_STORAGE_GKT2.removePrefix("gs://"))
                .build()

            FirebaseApp.initializeApp(context, options, appName)
        }
    }

    // --- UPDATED DATABASE PROVIDER ---
    /**
     * Firebase Realtime-Database
     */
    @TahapanReference
    @Provides
    // NEW: We need Context to initialize the secondary app
    fun provideTahapanFirebaseDatabaseReference(
        @ApplicationContext context: Context,
        sharedPrefs: SharedPreferences,
    ): DatabaseReference {
        // NEW: Capture the branch
        val selectedBranch = sharedPrefs.getString(ConstsSharedPrefs.SELECTED_BRANCH,
            ConstsSharedPrefs.BRANCH_GKT1) ?: ConstsSharedPrefs.BRANCH_GKT1

        // 1. Get the correct App Engine (JSON vs. Manual)
        val firebaseApp = getFirebaseApp(context, selectedBranch)

        // 2 . Get the Reference using the specific App
        // Note: We use getInstance(app) to ensure Auth works for that specific project
        val firebaseUrl = if (selectedBranch == ConstsSharedPrefs.BRANCH_GKT1)
            GriyaNodes.FIREBASE_RDB_GKT1 else GriyaNodes.FIREBASE_RDB_GKT2
        val rootReference = FirebaseDatabase.getInstance(firebaseApp, firebaseUrl).reference

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
    fun provideTahapanStorageReference(
        @ApplicationContext context: Context,
        sharedPrefs: SharedPreferences,
    ): StorageReference {
        // 1. Determine which branch (Storage Bucket) to use
        val selectedBranch = sharedPrefs.getString(ConstsSharedPrefs.SELECTED_BRANCH,
            ConstsSharedPrefs.BRANCH_GKT1) ?: ConstsSharedPrefs.BRANCH_GKT1

        val firebaseApp = getFirebaseApp(context, selectedBranch)

        // Use the specific App and URL
        val bucketUrl = when (selectedBranch) {
            ConstsSharedPrefs.BRANCH_GKT1 -> GriyaNodes.FIREBASE_STORAGE_GKT1
            ConstsSharedPrefs.BRANCH_GKT2 -> GriyaNodes.FIREBASE_STORAGE_GKT2
            else -> GriyaNodes.FIREBASE_STORAGE_GKT1 // Fallback
        }

        // 2. Initialize Storage with the specific bucket URL
        // Note: We use getInstance(url) just like we did for the Database
        val rootReference = FirebaseStorage.getInstance(firebaseApp, bucketUrl).reference

        // 3. Continue with your existing logic...
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