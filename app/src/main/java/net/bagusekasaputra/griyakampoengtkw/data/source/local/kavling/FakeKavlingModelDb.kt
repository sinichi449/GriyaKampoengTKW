package net.bagusekasaputra.griyakampoengtkw.data.source.local.kavling

import net.bagusekasaputra.griyakampoengtkw.data.source.model.BlockModel
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
                when(blockModel.kode) {
                    "A" -> {
                        kavlings.add(
                            KavlingModel(kode = "${blockModel.kode}$i", warna = blockModel.warna?: "Black", isActive = true,
                                ukuran = "6x12", type = "Type 36")
                        )
                    }
                    "B" -> {
                        kavlings.add(
                            KavlingModel(kode = "${blockModel.kode}$i", warna = blockModel.warna?: "Black", isActive = true,
                                ukuran = "6x11", type = "Type 36")
                        )
                    }
                    else -> {
                        kavlings.add(
                            KavlingModel(kode = "${blockModel.kode}$i", warna = blockModel.warna?: "Black", isActive = true,
                                ukuran = "6x11", type = "Type 36")
                        )
                    }
                }
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

    override fun addKavling(block: BlockModel): Boolean {
        return true
    }

}