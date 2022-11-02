package net.bagusekasaputra.griyakampoengtkw.di

import com.google.firebase.database.DatabaseReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.bagusekasaputra.griyakampoengtkw.data.repository.BlockRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.DataDiriRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.repository.KavlingRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.source.local.kavling.FakeKavlingModelDb
import net.bagusekasaputra.griyakampoengtkw.data.source.local.kavling.LocalKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.block.FirebaseBlockRepository
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.block.RemoteBlockRepository
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.datadiri.DataDiriRemoteRepositoryImpl
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.datadiri.IDataDiriRemoteRepository
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.kavling.FirebaseKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.kavling.RemoteKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository

@Module
@InstallIn(SingletonComponent::class)

object RepositoryModule {

    // Block Repository
    @Provides
    fun provideBlockRepository(remoteBlockRepository: RemoteBlockRepository): BlockRepository {
        return BlockRepositoryImpl(remoteBlockRepository)
    }

    @Provides
    fun provideRemoteBlockRepository(databaseReference: DatabaseReference): RemoteBlockRepository {
        return FirebaseBlockRepository(databaseReference)
    }

    // Kavling Repository
    @Provides
    fun provideKavlingRepository(remoteKavlingRepository: RemoteKavlingRepository): KavlingRepository {
        return KavlingRepositoryImpl(remoteKavlingRepository)
    }

    @Provides
    fun provideLocalKavlingRepository(): LocalKavlingRepository {
        return FakeKavlingModelDb()
    }

    @Provides
    fun provideRemoteKavlingRepository(databaseReference: DatabaseReference): RemoteKavlingRepository {
        return FirebaseKavlingRepository(databaseReference)
    }

    // Data Diri Repository
    @Provides
    fun provideDataDiriRepository(iDataDiriRemoteRepository: IDataDiriRemoteRepository): DataDiriRepository {
        return DataDiriRepositoryImpl(iDataDiriRemoteRepository)
    }

    @Provides
    fun provideIDataDiriFirebaseRepository(databaseReference: DatabaseReference): IDataDiriRemoteRepository {
        return DataDiriRemoteRepositoryImpl(databaseReference)
    }
}