package net.bagusekasaputra.griyakampoengtkw.di

import android.content.Context
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ActivityComponent::class, FragmentComponent::class)
object ActivityModule {

    @Provides
    fun provideSharedPreference(@ApplicationContext ctx: Context)
        = PreferenceManager.getDefaultSharedPreferences(ctx)
}