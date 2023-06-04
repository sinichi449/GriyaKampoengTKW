package net.bagusekasaputra.griyakampoengtkw.di

import android.content.SharedPreferences
import com.google.firebase.database.DatabaseReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import net.bagusekasaputra.griyakampoengtkw.cache.CacheInitializer
import net.bagusekasaputra.griyakampoengtkw.cache.DefaultCacheInitializer
import net.bagusekasaputra.griyakampoengtkw.data.FirebaseTahapanRepository
import net.bagusekasaputra.griyakampoengtkw.repository.TahapanRepository

@Module
@InstallIn(ViewModelComponent::class)
object StartupModule {

    @Provides
    fun provideTahapanRepository(@RootReference rootDatabaseReference: DatabaseReference): TahapanRepository {
        return FirebaseTahapanRepository(rootDatabaseReference)
    }

    @Provides
    fun provideCacheInitializer(
        sharedPreferences: SharedPreferences,
    ): CacheInitializer {
        return DefaultCacheInitializer(sharedPreferences)
    }

}