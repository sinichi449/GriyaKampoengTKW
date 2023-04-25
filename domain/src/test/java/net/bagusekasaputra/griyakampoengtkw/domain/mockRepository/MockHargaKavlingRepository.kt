package net.bagusekasaputra.griyakampoengtkw.domain.mockRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaKavlingRepository

class MockHargaKavlingRepository: HargaKavlingRepository {

    private val mapHargaKavling = mapOf<String, HargaKavling?>(
        Pair("A2", HargaKavling("A2", "320,000,000", "0")),
        Pair("A4", HargaKavling("A4", "230,000,000", "0")),
        Pair("A7", HargaKavling("A7", "230,000,000", "0")),
        Pair("A9", HargaKavling("A9", "230,000,000", "0")),
        Pair("A14", HargaKavling("A14", "230,000,000", "0")),
        Pair("B5", HargaKavling("B5", "220,000,000", "0")),
        Pair("B6", HargaKavling("B6", "220,000,000", "0")),
        Pair("B11", HargaKavling("B11", "380,000,000", "0")),
        Pair("B12", HargaKavling("B12", "220,000,000", "0")),
        Pair("B16", HargaKavling("B16", "220,000,000", "0")),
        Pair("B17", HargaKavling("B17", "220,000,000", "0")),
        Pair("B18", HargaKavling("B18", "900,000", "0")),
    )

    override fun getBatchOnline(listKavling: List<String>): Flow<Result<Map<String, HargaKavling?>?>> {
        return flow {
            val result = mutableMapOf<String, HargaKavling?>()

            listKavling.forEach { kavling ->
                result[kavling] = mapHargaKavling[kavling]
            }

            emit(Result.success(result))
        }
    }

    override fun getHargaKavling(
        kavlingKode: String,
        offline: Boolean,
    ): Flow<Result<HargaKavling?>> {
        return flow {
            emit(Result.success(mapHargaKavling[kavlingKode]))
        }
    }

    override fun getSingleHargaKavlingForPembayaran(kavlingKode: String): Flow<HargaKavling> {
        TODO("Not yet implemented")
    }

    override fun addHargaKavling(hargaKavling: HargaKavling): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun deleteHargaKavling(kavlingKode: String): Flow<Result<Nothing?>> {
        TODO("Not yet implemented")
    }

}