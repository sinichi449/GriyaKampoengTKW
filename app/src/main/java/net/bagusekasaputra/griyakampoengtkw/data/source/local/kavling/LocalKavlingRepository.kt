package net.bagusekasaputra.griyakampoengtkw.data.source.local.kavling

import net.bagusekasaputra.griyakampoengtkw.data.model.BlockModel
import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingModel

interface LocalKavlingRepository {

    fun getKavlingByBlock(block: BlockModel): List<KavlingModel>

    fun addKavling(block: BlockModel): Boolean
}