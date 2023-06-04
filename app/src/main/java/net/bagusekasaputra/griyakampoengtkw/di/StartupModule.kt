package net.bagusekasaputra.griyakampoengtkw.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import net.bagusekasaputra.griyakampoengtkw.cache.CacheInitializer
import net.bagusekasaputra.griyakampoengtkw.cache.DefaultCacheInitializer

@Module
@InstallIn(ViewModelComponent::class)
interface StartupModule {

    @Binds
    fun bindsCacheInitializer(defaultCacheInitializer: DefaultCacheInitializer): CacheInitializer

}