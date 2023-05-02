package net.bagusekasaputra.griyakampoengtkw.di

import android.content.ContentResolver
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
object MiscModule {

    @ExternalDir
    @Provides
    fun provideExternalFilesDir(@ApplicationContext applicationContext: Context): File? {
        return applicationContext.getExternalFilesDir(null)
    }

    @Provides
    fun provideContentResolver(@ApplicationContext applicationContext: Context): ContentResolver {
        return applicationContext.contentResolver
    }

    @InternalDir
    @Provides
    fun provideInternalFilesDir(@ApplicationContext ctx: Context): File {
        return ctx.filesDir
    }

}

@Qualifier
annotation class InternalDir

@Qualifier
annotation class ExternalDir