package net.bagusekasaputra.griyakampoengtkw.domain.usecase.block

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase

class GetAllBlocksUseCase(
    private val blockRepository: BlockRepository
): UseCase<GetAllBlocksUseCase.Request, GetAllBlocksUseCase.Response>() {

    object Request: UseCase.Request

    data class Response(val result: Result<List<Block>?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return blockRepository.getAllBlocks().map { result ->
            val newResult = result.map { listBlock ->
                sortBlocks(listBlock)
            }

            Response(newResult)
        }
    }

    private fun sortBlocks(listBlock: List<Block>?): List<Block>? {
        return listBlock?.sortedBy {
            it.kode.lowercase()
        }
    }
}