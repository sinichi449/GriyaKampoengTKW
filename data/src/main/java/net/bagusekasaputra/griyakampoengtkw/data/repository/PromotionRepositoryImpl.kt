package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemotePromotionDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Promotion
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PromotionRepository

class PromotionRepositoryImpl(
    private val remoteDataSource: RemotePromotionDataSource,
): PromotionRepository {

    override fun get(): Flow<Result<Promotion?>> {
        return flow {
            val remoteResult = remoteDataSource.get()
            if (remoteResult.isSuccess) {
                val promotion = remoteResult.getOrNull()?.let {
                    MyObjectMapper.mapPromotion(it)
                }

                emit(Result.success(promotion))
            } else {
                val exception = remoteResult.exceptionOrNull()
                    ?: Throwable("Kesalahan tidak diketahui dalam mendapatkan objek Promosi!")

                emit(Result.failure(exception))
            }
        }
    }
}