package net.bagusekasaputra.griyakampoengtkw.di

import com.google.firebase.database.DatabaseReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import net.bagusekasaputra.griyakampoengtkw.CacheAccumulatorForProgressKavling
import net.bagusekasaputra.griyakampoengtkw.data.remote.InitRemoteImpl
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BaselinePembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.interfaces.remote.InitRemote

@Module
@InstallIn(ActivityComponent::class)
object DataSourceDeveloperModule {

    @Provides
    fun provideInitRemote(databaseReference: DatabaseReference): InitRemote {
        return InitRemoteImpl(databaseReference)
    }

    @Provides
    fun provideCacheAccumulatorForProgressKavling(
        blockRepository: BlockRepository,
        kavlingRepository: KavlingRepository,
        baselinePembayaranRepository: BaselinePembayaranRepository,
        dataDiriRepository: DataDiriRepository,
        pembayaranRepository: PembayaranRepository,
    ): CacheAccumulatorForProgressKavling {
        return CacheAccumulatorForProgressKavling(
            blockRepository,
            kavlingRepository,
            baselinePembayaranRepository,
            dataDiriRepository,
            pembayaranRepository,
        )
    }

}