package net.bagusekasaputra.griyakampoengtkw.di

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.bagusekasaputra.griyakampoengtkw.MyCacheInitializer
import net.bagusekasaputra.griyakampoengtkw.interfaces.CacheInitializer

@Module
@InstallIn(ActivityComponent::class, FragmentComponent::class, SingletonComponent::class)
object ActivityModule {

    @Provides
    fun provideSharedPreference(@ApplicationContext ctx: Context): SharedPreferences
        = PreferenceManager.getDefaultSharedPreferences(ctx)

    @Provides
    fun provideCacheInitializer(sharedPreferences: SharedPreferences): CacheInitializer {
        return MyCacheInitializer(sharedPreferences)
    }

}