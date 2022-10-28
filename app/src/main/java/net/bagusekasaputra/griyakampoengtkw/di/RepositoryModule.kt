package net.bagusekasaputra.griyakampoengtkw.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.bagusekasaputra.griyakampoengtkw.data.repository.BlockRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.KavlingRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.source.local.block.FakeBlockModelsDb
import net.bagusekasaputra.griyakampoengtkw.data.source.local.block.LocalBlockRepository
import net.bagusekasaputra.griyakampoengtkw.data.source.local.kavling.FakeKavlingModelDb
import net.bagusekasaputra.griyakampoengtkw.data.source.local.kavling.LocalKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository

@Module
@InstallIn(SingletonComponent::class)

object RepositoryModule {

    @Provides
    fun provideLocalKavlingRepository(): LocalKavlingRepository {
        return FakeKavlingModelDb()
    }

    @Provides
    fun provideKavlingRepository(localKavlingRepository: LocalKavlingRepository): KavlingRepository {
        return KavlingRepositoryImpl(localKavlingRepository)
    }

    @Provides
    fun provideBlockRepository(localBlockRepository: LocalBlockRepository): BlockRepository {
        return BlockRepositoryImpl(localBlockRepository)
    }

    @Provides
    fun provideLocalBlockRepository(): LocalBlockRepository {
        return FakeBlockModelsDb()
    }
}