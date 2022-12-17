package net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageDataDiri

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ImageDataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageDataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase

class GetImageDataDiriByKavlingKodeUseCase(
    private val imageDataDiriRepository: ImageDataDiriRepository
): UseCase<GetImageDataDiriByKavlingKodeUseCase.Request, GetImageDataDiriByKavlingKodeUseCase.Response>() {

    data class Request(val kavlingKode: String): UseCase.Request

    data class Response(val result: Result<ImageDataDiri?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return imageDataDiriRepository.getByKavlingKode(request.kavlingKode).map {
            Response(it)
        }
    }
}