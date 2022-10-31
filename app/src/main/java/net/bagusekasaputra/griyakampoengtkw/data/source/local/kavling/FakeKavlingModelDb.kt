package net.bagusekasaputra.griyakampoengtkw.data.source.local.kavling

import net.bagusekasaputra.griyakampoengtkw.data.source.local.block.BlockModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeKavlingModelDb @Inject constructor(

): LocalKavlingRepository {

    private val blockA = ArrayList<KavlingModel>()
    private val blockB = ArrayList<KavlingModel>()
    private val blockC = ArrayList<KavlingModel>()

    private fun generatePseudoKavlingModel(kavlings: ArrayList<KavlingModel>, blockModel: BlockModel, limit: Int) {
        if (kavlings.size == 0) {
            for (i in 1 .. limit) {
                kavlings.add(
                    KavlingModel(kode = "${blockModel.kode}$i", warna = blockModel.warna, isActive = true)
                )
            }
        }
    }

    override fun getKavlingByBlock(block: BlockModel): List<KavlingModel> {
        return when (block.kode) {
            "A" -> {
                generatePseudoKavlingModel(blockA, block, 14)
                blockA
            }
            "B" -> {
                generatePseudoKavlingModel(blockB, block, 20)
                blockB
            } else -> {
                generatePseudoKavlingModel(blockC, block, 9)
                blockC
            }
        }
    }

    private fun newKavling(block: BlockModel, kavlings: ArrayList<KavlingModel>) {
        val lastKavlingIndex = kavlings.size + 1
        val newKavlingModel = KavlingModel(
            kode = "${block.kode}$lastKavlingIndex",
            warna = block.warna,
            isActive = true
        )
        kavlings.add(newKavlingModel)
    }

    override fun addKavling(block: BlockModel): Boolean {
        getKavlingByBlock(block)
        when (block.kode) {
            "A" -> {
                newKavling(block, blockA)
            }
            "B" -> {
                newKavling(block, blockB)
            }
            else -> {
                newKavling(block, blockC)
            }
        }

        return true
    }

}