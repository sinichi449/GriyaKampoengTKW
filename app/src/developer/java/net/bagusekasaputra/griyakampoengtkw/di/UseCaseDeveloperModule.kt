package net.bagusekasaputra.griyakampoengtkw.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaran.GetListPembayaranBulananAsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BaselinePembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoPembayaranRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseDeveloperModule {

    /**
     * Pembayaran Bulanan
     */
    @Provides
    fun provideGetListPembayaranBulananUseCase(
        pembayaranRepository: PembayaranRepository,
        baselinePembayaranRepository: BaselinePembayaranRepository,
        hargaKavlingRepository: HargaKavlingRepository,
        fotoPembayaranRepository: FotoPembayaranRepository,
    ): GetListPembayaranBulananAsyncUseCase {
        return GetListPembayaranBulananAsyncUseCase(pembayaranRepository, baselinePembayaranRepository, hargaKavlingRepository, fotoPembayaranRepository)
    }

}