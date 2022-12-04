package net.bagusekasaputra.griyakampoengtkw.domain.mockRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository

class MockDataDiriRepository: DataDiriRepository {

    override fun getDataDiri(kavlingKode: String, offline: Boolean): Flow<Result<DataDiri?>> {
        return flow {
            val listDataDiri = listOf(
                DataDiri("Andi Setya Budi", "", "", "", "", "", ""),
                DataDiri("Iwan Ferdiyanto", "", "", "", "", "", ""),
                DataDiri("Norma Fiki Sugiarta", "", "", "", "", "", ""),
            )
            val dataDiri: DataDiri? = when (kavlingKode) {
                "A2" -> listDataDiri[0]
                "A3" -> listDataDiri[1]
                "A4" -> listDataDiri[2]
                else -> null
            }

            emit(Result.success(dataDiri))
        }
    }

    override fun addDataDiri(kavlingKode: String, dataDiri: DataDiri): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun deleteDataDiri(kavlingKode: String): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

}