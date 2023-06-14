@file:Suppress("DEPRECATION")

package net.bagusekasaputra.griyakampoengtkw.di

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.bagusekasaputra.griyakampoengtkw.presentation.util.GriyaNodes

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideSharedPreference(@ApplicationContext ctx: Context): SharedPreferences
            = PreferenceManager.getDefaultSharedPreferences(ctx)

    @Provides
    fun provideLocalBroadcastManager(@ApplicationContext context: Context): LocalBroadcastManager {
        return LocalBroadcastManager.getInstance(context)
    }

    @Provides
    fun provideContentResolver(@ApplicationContext applicationContext: Context): ContentResolver {
        return applicationContext.contentResolver
    }

    @RootReference
    @Provides
    fun provideRootStorageReference() = FirebaseStorage.getInstance().reference

    @RootReference
    @Provides
    fun providesRootFirebaseDatabaseReference()
            = FirebaseDatabase.getInstance(GriyaNodes.firebaseUrl).reference

}