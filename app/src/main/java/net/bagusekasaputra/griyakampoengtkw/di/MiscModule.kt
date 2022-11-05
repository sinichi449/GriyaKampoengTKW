package net.bagusekasaputra.griyakampoengtkw.di

import android.content.ContentResolver
import android.content.Context
import android.os.Environment
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
    fun providePictureDirectory(@ApplicationContext applicationContext: Context): File? {
        return applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    }

    @Provides
    fun provideContentResolver(@ApplicationContext applicationContext: Context): ContentResolver {
        return applicationContext.contentResolver
    }

}