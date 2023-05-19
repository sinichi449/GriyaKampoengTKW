package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.promotion

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Promotion
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PromotionRepository

class GetPromotionMessageAsyncUseCase(
    private val promotionRepository: PromotionRepository,
): AsyncUseCase<GetPromotionMessageAsyncUseCase.Request, Promotion>() {

    object Request: AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<Promotion?>> {
        return promotionRepository.get()
    }
}