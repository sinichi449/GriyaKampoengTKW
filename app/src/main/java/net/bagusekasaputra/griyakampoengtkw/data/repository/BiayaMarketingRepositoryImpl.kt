package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMarketingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiayaMarketingRepositoryImpl @Inject constructor(

): BiayaMarketingRepository {

    // Still mock!
    private val listBiayaMarketing = ArrayList<BiayaMarketing>()

    private fun generateListBiayaMarketing() =
        mutableListOf<BiayaMarketing>(
            BiayaMarketing(1, "D1", "Sofa", "500000"),
            BiayaMarketing(2, "D1", "Item 1", "5000"),
            BiayaMarketing(3, "D1", "Item 2", "20000"),
            BiayaMarketing(4, "D1", "Lorem ipsum dolor sit amet", "31500")
        )

    init {
        if (listBiayaMarketing.isEmpty()) {
            generateListBiayaMarketing().forEach {
                listBiayaMarketing.add(it)
            }
        }
    }

    override fun getAllByKavlingKode(kavlingKode: String): Flow<Result<List<BiayaMarketing>?>> {
        return flow {
            emit(Result.success(listBiayaMarketing))
        }
    }

    override fun addBiayaMarketing(biayaMarketing: BiayaMarketing): Flow<Result<Nothing?>> {
        return flow {
            listBiayaMarketing.add(biayaMarketing)

            emit(Result.success(null))
        }
    }

    override fun update(
        oldBiayaMarketing: BiayaMarketing,
        newBiayaMarketing: BiayaMarketing
    ): Flow<Result<Nothing?>> {
        TODO("Not yet implemented")
    }

    override fun deleteByKavlingKode(kavlingKode: String): Flow<Result<Nothing?>> {
        TODO("Not yet implemented")
    }
}