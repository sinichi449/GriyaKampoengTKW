package net.bagusekasaputra.griyakampung.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.bagusekasaputra.griyakampung.data.repository.KavlingRepositoryImpl
import net.bagusekasaputra.griyakampung.data.source.local.LocalKavlingRepository
import net.bagusekasaputra.griyakampung.data.source.local.LocalKavlingRepositoryImpl
import net.bagusekasaputra.griyakampung.domain.repository.KavlingRepository

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