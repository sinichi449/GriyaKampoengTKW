package net.bagusekasaputra.griyakampoengtkw.di

import android.content.ContentResolver
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File

@Module
@InstallIn(SingletonComponent::class)
object MiscModule {

    @Provides
    fun provideExternalFilesDir(@ApplicationContext applicationContext: Context): File? {
        return applicationContext.getExternalFilesDir(null)
    }

    @Provides
    fun provideContentResolver(@ApplicationContext applicationContext: Context): ContentResolver {
        return applicationContext.contentResolver
    }

}