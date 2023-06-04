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
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes.Companion.firebaseUrl
import javax.inject.Qualifier
import javax.inject.Singleton

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

        return Room.databaseBuilder(
            appContext, MyRoomDatabase::class.java, "griya_kampoeng_tkw.db"
        )
            .fallbackToDestructiveMigration()
            .addCallback(onDestructiveMigrationCallback)
            .build()
    }

    @RootReference
    @Provides
    fun providesRootFirebaseDatabaseReference()
        = FirebaseDatabase.getInstance(firebaseUrl).reference

    @TahapanReference
    @Provides
    fun provideTahapanFirebaseDatabaseReference(sharedPrefs: SharedPreferences): DatabaseReference {
        val rootReference = FirebaseDatabase.getInstance(firebaseUrl).reference

        val selectedTahapan = sharedPrefs.getString(ConstsSharedPrefs.SELECTED_TAHAPAN, "TAHAP_1")!!
        return rootReference.child(selectedTahapan)
    }


    @RootReference
    @Provides
    fun provideRootStorageReference() = FirebaseStorage.getInstance().reference

    @TahapanReference
    @Provides
    fun provideTahapanStorageReference(sharedPrefs: SharedPreferences): StorageReference {
        val rootReference = FirebaseStorage.getInstance().reference

        val selectedTahapan = sharedPrefs.getString(ConstsSharedPrefs.SELECTED_TAHAPAN, "TAHAP_1")!!
        return rootReference.child(selectedTahapan)
    }

    @Singleton
    @Provides
    fun provideCacheHelper(
        localMetadataDataSource: LocalMetadataDataSource,
        remoteMetadataDataSource: RemoteMetadataDataSource,
    ): CacheHelper {
        return CacheHelper(localMetadataDataSource, remoteMetadataDataSource)
    }

}

@Qualifier
annotation class RootReference

@Qualifier
annotation class TahapanReference