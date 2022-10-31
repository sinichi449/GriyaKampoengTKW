package net.bagusekasaputra.griyakampoengtkw.data.source.local.block

import net.bagusekasaputra.griyakampoengtkw.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeBlockModelsDb @Inject constructor(
) : LocalBlockRepository {

    private val blocks = ArrayList<BlockModel>()

    init {
        blocks.apply {
            add(BlockModel(kode = "A", warna = R.color.abang))
            add(BlockModel(kode = "B", warna = R.color.oren_1))
            add(BlockModel(kode = "C", warna = R.color.oren_2))
        }
    }

    override fun getAllBlockModels(): List<BlockModel> {
        return blocks
    }

}