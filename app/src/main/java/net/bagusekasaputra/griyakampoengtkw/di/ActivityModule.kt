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
import kotlinx.coroutines.Dispatchers
import net.bagusekasaputra.griyakampoengtkw.MyCacheInitializer
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BaselinePembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.interfaces.CacheInitializer

@Module
@InstallIn(ActivityComponent::class, FragmentComponent::class, SingletonComponent::class)
object ActivityModule {

    @Provides
    fun provideSharedPreference(@ApplicationContext ctx: Context): SharedPreferences
        = PreferenceManager.getDefaultSharedPreferences(ctx)

    @Provides
    fun provideCacheInitializer(
        sharedPreferences: SharedPreferences,
        blockRepository: BlockRepository,
        kavlingRepository: KavlingRepository,
        dataDiriRepository: DataDiriRepository,
        baselinePembayaranRepository: BaselinePembayaranRepository,
        pembayaranRepository: PembayaranRepository,
    ): CacheInitializer {
        return MyCacheInitializer(sharedPreferences, Dispatchers.IO, blockRepository, kavlingRepository, dataDiriRepository, baselinePembayaranRepository, pembayaranRepository)
    }

}