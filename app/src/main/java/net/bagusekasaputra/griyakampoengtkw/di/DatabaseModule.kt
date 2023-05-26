package net.bagusekasaputra.griyakampoengtkw.di

import android.content.Context
import androidx.room.Room
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
import net.bagusekasaputra.griyakampoengtkw.data.CacheHelper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteMetadataDataSource
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes.Companion.firebaseUrl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideMyRoomDatabase(@ApplicationContext appContext: Context): MyRoomDatabase {
        return Room.databaseBuilder(
            appContext, MyRoomDatabase::class.java, "griya_kampoeng_tkw.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providesFirebaseDatabaseReference(): DatabaseReference {
        return FirebaseDatabase.getInstance(firebaseUrl).reference
    }

    @Provides
    fun provideStorageReference(): StorageReference {
        return FirebaseStorage.getInstance().reference
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