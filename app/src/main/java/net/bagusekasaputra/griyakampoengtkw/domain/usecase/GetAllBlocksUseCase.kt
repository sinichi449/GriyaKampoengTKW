package net.bagusekasaputra.griyakampoengtkw.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllBlocksUseCase @Inject constructor(
    private val blockRepository: BlockRepository
): UseCase<GetAllBlocksUseCase.Request, GetAllBlocksUseCase.Response>() {

    object Request: UseCase.Request

    data class Response(val data: List<Block>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return blockRepository.getAllBlocks().map {
            Response(it)
        }
    }

}