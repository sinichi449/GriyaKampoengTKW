package net.bagusekasaputra.griyakampoengtkw.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddNewBlockUseCase @Inject constructor(
    private val blockRepository: BlockRepository
): UseCase<AddNewBlockUseCase.Request, AddNewBlockUseCase.Response>() {

    data class Request(val block: Block): UseCase.Request

    data class Response(val result: Result<Boolean>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return blockRepository.addBlock(request.block).map {
            Response(it)
        }
    }
}