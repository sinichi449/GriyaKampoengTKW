package net.bagusekasaputra.griyakampoengtkw.data.source.local.kavling

import net.bagusekasaputra.griyakampoengtkw.data.source.local.block.BlockModel

interface LocalKavlingRepository {

    fun getKavlingByBlock(block: BlockModel): List<KavlingModel>
}