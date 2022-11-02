package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.R
import net.bagusekasaputra.griyakampoengtkw.data.source.local.kavling.KavlingModel
import net.bagusekasaputra.griyakampoengtkw.data.source.local.kavling.LocalKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.block.BlockModel
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KavlingRepositoryImpl @Inject constructor(
    private val localKavlingRepository: LocalKavlingRepository
): KavlingRepository {

    override fun getKavlingByBlock(block: Block): Flow<List<Kavling>> {
        return flow {
            val blockModel = BlockModel(
                kode = block.kode,
                warna = BlockRepositoryImpl.parseBlockWarna(block.warna?: R.color.black)
            )
            val kavlings = localKavlingRepository.getKavlingByBlock(blockModel).map {
                mapKavling(it)
            }
            emit(kavlings)
        }
    }

    override fun addKavling(block: Block): Flow<Boolean> {
        return flow {
            val result = localKavlingRepository.addKavling(
                BlockModel(
                    kode = block.kode,
                    warna = BlockRepositoryImpl.parseBlockWarna(block.warna?: R.color.black)
                )
            )
            emit(result)
        }
    }

    private fun mapKavling(kavlingModel: KavlingModel): Kavling {
        return Kavling(
            kavlingModel.kode,
            kavlingModel.isActive,
            BlockRepositoryImpl.parseBlockWarna(kavlingModel.warna)?: R.color.black,
            kavlingModel.ukuran,
            kavlingModel.type
        )
    }

}