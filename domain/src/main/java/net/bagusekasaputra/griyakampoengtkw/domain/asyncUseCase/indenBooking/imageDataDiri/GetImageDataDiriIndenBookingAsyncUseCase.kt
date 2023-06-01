package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.imageDataDiri

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.ImageDataDiriIndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageDataDiriIndenBookingRepository

class GetImageDataDiriIndenBookingAsyncUseCase(
    private val imageDataDiriRepository: ImageDataDiriIndenBookingRepository,
): AsyncUseCase<GetImageDataDiriIndenBookingAsyncUseCase.Request, ImageDataDiriIndenBooking>() {

    data class Request(val keyId: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<ImageDataDiriIndenBooking?>> {
        return flow {
            emit(imageDataDiriRepository.get(request.keyId))
        }
    }

}