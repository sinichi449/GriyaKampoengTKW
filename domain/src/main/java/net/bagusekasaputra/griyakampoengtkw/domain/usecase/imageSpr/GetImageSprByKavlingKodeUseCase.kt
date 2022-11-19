package net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageSpr

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ImageSpr
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageSprRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase

class GetImageSprByKavlingKodeUseCase(
    private val imageSprRepository: ImageSprRepository,
): UseCase<GetImageSprByKavlingKodeUseCase.Request, GetImageSprByKavlingKodeUseCase.Response>() {

    data class Request(val kavlingKode: String): UseCase.Request

    data class Response(val result: Result<ImageSpr?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return imageSprRepository.getByKavlingKode(request.kavlingKode).map {
            Response(it)
        }
    }
}