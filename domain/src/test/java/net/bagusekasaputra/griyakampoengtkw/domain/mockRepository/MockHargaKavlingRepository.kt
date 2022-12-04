package net.bagusekasaputra.griyakampoengtkw.domain.mockRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaKavlingRepository

class MockHargaKavlingRepository: HargaKavlingRepository {

    override fun getHargaKavling(
        kavlingKode: String,
        offline: Boolean,
    ): Flow<Result<HargaKavling?>> {
        return flow {
            val hargaKavling = when (kavlingKode) {
                "A2" -> HargaKavling("A2", "250,000,000", "0")
                "A3" -> HargaKavling("A3", "210,000,000", "0")
                "A4" -> HargaKavling("A4", "230,000,000", "0")
                else -> null
            }

            emit(Result.success(hargaKavling))
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