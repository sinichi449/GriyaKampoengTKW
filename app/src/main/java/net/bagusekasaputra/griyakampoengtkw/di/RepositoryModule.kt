package net.bagusekasaputra.griyakampoengtkw.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.bagusekasaputra.griyakampoengtkw.data.repository.KavlingRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.source.local.LocalKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.data.source.local.LocalKavlingRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository

@Module
@InstallIn(SingletonComponent::class)

object RepositoryModule {

    @Provides
    fun provideLocalRepository(): LocalKavlingRepository {
        return LocalKavlingRepositoryImpl()
    }

    @Provides
    fun provideKavlingRepository(localKavlingRepository: LocalKavlingRepository): KavlingRepository {
        return KavlingRepositoryImpl(localKavlingRepository)
    }
}