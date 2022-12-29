package net.bagusekasaputra.griyakampoengtkw.domain.mockRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository

class MockDataDiriRepository: DataDiriRepository {

    private val mapDataDiri = mapOf(
        Pair("A1", null),
        Pair("A2", DataDiri("Siti Hartini", "", "", "", "", "", "")),
        Pair("A4", DataDiri("Eva Emilia Carolina BR Sinuraya", "", "", "", "", "", "")),
        Pair("A7", DataDiri("Windi Novianti", "", "", "", "", "", "")),
        Pair("A9", DataDiri("Nurhayati", "", "", "", "", "", "")),
        Pair("A14", DataDiri("MD Mohidul", "", "", "", "", "", "")),
        Pair("B5", DataDiri("Ria Eka Sari", "", "", "", "", "", "")),
        Pair("B6", DataDiri("Laela Nurkumalasari", "", "", "", "", "", "")),
        Pair("B11", DataDiri("Hesti Milawati", "", "", "", "", "", "")),
        Pair("B12", DataDiri("Sri Wahyuni Bahtiyar", "", "", "", "", "", "")),
        Pair("B16", DataDiri("Budiyarti", "", "", "", "", "", "")),
        Pair("B17", DataDiri("Iin Handayani", "", "", "", "", "", "")),
        Pair("B18", DataDiri("Duwi Indah Setiyorini", "", "", "", "", "", "")),
    )

    override fun getBatch(listKavling: List<String>): Flow<Result<Map<String, DataDiri?>?>> {
        return flow {
            val result = mutableMapOf<String, DataDiri?>()

            listKavling.forEach { kavling ->
                result[kavling] = mapDataDiri[kavling]
            }

            emit(Result.success(result))
        }
    }

    override fun getDataDiri(kavlingKode: String, offline: Boolean): Flow<Result<DataDiri?>> {
        return flow {
            emit(Result.success(mapDataDiri[kavlingKode]))
        }
    }

    override fun addDataDiri(kavlingKode: String, dataDiri: DataDiri): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun deleteDataDiri(kavlingKode: String): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

}