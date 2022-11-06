package net.bagusekasaputra.griyakampoengtkw.domain.usecase.imageDataDiri

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ImageDataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.repository.ImageDataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteImageDataDiriUseCase @Inject constructor(
    private val imageDataDiriRepository: ImageDataDiriRepository,
): UseCase<DeleteImageDataDiriUseCase.Request, DeleteImageDataDiriUseCase.Response>() {

    data class Request(val imageDataDiri: ImageDataDiri): UseCase.Request

    data class Response(val result: Result<Boolean>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return imageDataDiriRepository.deleteImage(request.imageDataDiri)
            .map {
                Response(it)
            }
    }
}