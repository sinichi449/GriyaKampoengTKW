package net.bagusekasaputra.griyakampoengtkw.domain.usecase.block

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddNewBlockUseCase @Inject constructor(
    private val blockRepository: BlockRepository
): UseCase<AddNewBlockUseCase.Request, AddNewBlockUseCase.Response>() {

    data class Request(val block: Block): UseCase.Request

    data class Response(val result: Result<Nothing?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        // Adding hash sign into the warna
        val addedHashBlock = Block(
            kode = request.block.kode,
            warna = "#${request.block.warna}",
        )

        return blockRepository.addBlock(addedHashBlock).map {
            Response(it)
        }
    }
}