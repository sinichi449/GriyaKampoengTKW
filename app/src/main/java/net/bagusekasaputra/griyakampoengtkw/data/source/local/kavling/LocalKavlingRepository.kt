package net.bagusekasaputra.griyakampoengtkw.data.source.local.kavling

import net.bagusekasaputra.griyakampoengtkw.data.source.model.BlockModel

interface LocalKavlingRepository {

    fun getKavlingByBlock(block: BlockModel): List<KavlingModel>

    fun addKavling(block: BlockModel): Boolean
}