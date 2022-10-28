package net.bagusekasaputra.griyakampoengtkw.data.source.local.kavling

import net.bagusekasaputra.griyakampoengtkw.data.source.local.block.BlockModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeKavlingModelDb @Inject constructor(

): LocalKavlingRepository {

    private fun generatePseudoKavlingModel(blockModel: BlockModel): ArrayList<KavlingModel> {
        val models = ArrayList<KavlingModel>()
        for (i in 1 .. 30) {
            models.add(
                KavlingModel(kode = "${blockModel.kode}$i", warna = blockModel.warna, isActive = true)
            )
        }

        return models
    }

    override fun getKavlingByBlock(block: BlockModel): List<KavlingModel> {
        return generatePseudoKavlingModel(block)
    }

}