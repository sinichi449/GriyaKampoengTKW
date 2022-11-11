package net.bagusekasaputra.griyakampoengtkw.domain.usecase.fotoKuitansi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoKuitansi
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoKuitansiRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetFotoKuitansiUseCase @Inject constructor(
    private val fotoKuitansiRepository: FotoKuitansiRepository,
): UseCase<GetFotoKuitansiUseCase.Request, GetFotoKuitansiUseCase.Response>() {

    data class Request(val kavlingKode: String): UseCase.Request

    data class Response(val result: Result<FotoKuitansi?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return fotoKuitansiRepository.getFoto(request.kavlingKode).map {
            Response(it)
        }
    }

}