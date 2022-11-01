package net.bagusekasaputra.griyakampoengtkw.di

import com.google.firebase.database.DatabaseReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.bagusekasaputra.griyakampoengtkw.data.repository.BlockRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.DataDiriRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.KavlingRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.source.local.block.FakeBlockModelsDb
import net.bagusekasaputra.griyakampoengtkw.data.source.local.block.LocalBlockRepository
import net.bagusekasaputra.griyakampoengtkw.data.source.local.kavling.FakeKavlingModelDb
import net.bagusekasaputra.griyakampoengtkw.data.source.local.kavling.LocalKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.DataDiriRemoteRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.IDataDiriRemoteRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository
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

    @Provides
    fun provideDataDiriRepository(iDataDiriRemoteRepository: IDataDiriRemoteRepository): DataDiriRepository {
        return DataDiriRepositoryImpl(iDataDiriRemoteRepository)
    }

    @Provides
    fun provideIDataDiriFirebaseRepository(databaseReference: DatabaseReference): IDataDiriRemoteRepository {
        return DataDiriRemoteRepositoryImpl(databaseReference)
    }
}