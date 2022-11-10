package net.bagusekasaputra.griyakampoengtkw.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.bagusekasaputra.griyakampoengtkw.data.source.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes.Companion.firebaseUrl

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideMyRoomDatabase(@ApplicationContext appContext: Context): MyRoomDatabase {
        return Room.databaseBuilder(
            appContext, MyRoomDatabase::class.java, "griya_kampoeng_tkw.db"
        ).build()
    }

    @Provides
    fun providesFirebaseDatabaseReference(): DatabaseReference {
        return FirebaseDatabase.getInstance(firebaseUrl).reference
    }

}