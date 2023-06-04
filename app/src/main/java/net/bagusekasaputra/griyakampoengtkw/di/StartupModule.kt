package net.bagusekasaputra.griyakampoengtkw.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import net.bagusekasaputra.griyakampoengtkw.cache.CacheInitializer
import net.bagusekasaputra.griyakampoengtkw.cache.DefaultCacheInitializer

@Module
@InstallIn(ViewModelComponent::class)
object StartupModule {

    @Provides
    fun provideCacheInitializer(
        sharedPreferences: SharedPreferences,
    ): CacheInitializer {
        return DefaultCacheInitializer(sharedPreferences)
    }

}