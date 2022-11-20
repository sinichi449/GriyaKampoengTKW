package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.block

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository

class GetAllBlocksAsyncUseCase(
    private val blockRepository: BlockRepository,
): AsyncUseCase<GetAllBlocksAsyncUseCase.Request, List<Block>?>() {

    data class Request(val offline: Boolean): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<Block>?>> {
        return blockRepository.getAllBlocks(request.offline).map { result ->
            // Sort the Blocks alphabetically
            result.map { listBlock ->
                sortBlocks(listBlock)
            }
        }
    }

    private fun sortBlocks(listBlock: List<Block>?): List<Block>? {
        return listBlock?.sortedBy {
            it.kode.lowercase()
        }
    }
}