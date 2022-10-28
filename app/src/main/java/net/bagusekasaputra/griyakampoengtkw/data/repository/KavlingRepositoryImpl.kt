package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.source.local.KavlingModel
import net.bagusekasaputra.griyakampoengtkw.data.source.local.LocalKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KavlingRepositoryImpl @Inject constructor(
    val localKavlingRepository: LocalKavlingRepository
): KavlingRepository {

    override fun getKavlingByKode(kode: String): Flow<List<Kavling>> {
        val kavlings = localKavlingRepository.getKavlingByKode(kode).map {
            mapKavling(it)
        }
        return flow {
            emit(kavlings)
        }
    }

    override fun addKavling(kode: String): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    private fun mapKavling(kavlingModel: KavlingModel): Kavling {
        return Kavling(
            kavlingModel.kode,
            kavlingModel.isActive
        )
    }

}