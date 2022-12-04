package net.bagusekasaputra.griyakampoengtkw.domain.mockRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository

class MockPembayaranRepository: PembayaranRepository {

    override fun getAllPembayaran(
        kavlingKode: String,
        offline: Boolean,
    ): Flow<Result<List<Pembayaran>?>> {
        return flow {
            val timeMillis = System.currentTimeMillis()

            val listPembayaranA2 = listOf(
                Pembayaran("ITJ 1", "06/05/2021", "40,000,000", "", 0.0, "", "-", timeMillis, true),
                Pembayaran("DP 1", "07/05/2021", "40,000,000", "", 0.0, "", "-", timeMillis, true),
                Pembayaran("DP 2", "08/05/2021", "50,000,000", "", 0.0, "", "-", timeMillis, true),
            )

            val listPembayaranA3 = listOf(
                Pembayaran("ITJ 1", "01/05/2021", "15,000,000", "", 0.0, "", "-", timeMillis, true),
                Pembayaran("DP 1", "01/05/2021", "15,000,000", "", 0.0, "", "-", timeMillis, true),
            )

            val listPembayaranA4 = listOf(
                Pembayaran("ITJ 1", "13/07/2022", "20,000,000", "", 0.0, "", "-", timeMillis, true),
                Pembayaran("DP 1", "13/07/2022", "20,000,000", "", 0.0, "", "-", timeMillis, true),
                Pembayaran("DP 2", "13/07/2022", "20,000,000", "", 0.0, "", "-", timeMillis, true),
                Pembayaran("DP 3", "13/07/2022", "20,000,000", "", 0.0, "", "-", timeMillis, true),
                Pembayaran("DP 4", "13/07/2022", "22,087,227", "", 0.0, "", "-", timeMillis, true),
            )

            val listPembayaran = when (kavlingKode) {
                "A2" -> listPembayaranA2
                "A3" -> listPembayaranA3
                "A4" -> listPembayaranA4
                else -> null
            }

            emit(Result.success(listPembayaran))
        }
    }

    override fun getAllOnline(kavlingKode: String): Flow<Result<List<Pembayaran>?>> {
        TODO("Not yet implemented")
    }

    override fun addPembayaran(
        kavlingKode: String,
        hargaKavling: Long,
        pembayaran: Pembayaran,
    ): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun updatePembayaran(
        kavlingKode: String,
        oldPembayaran: Pembayaran,
        newPembayaran: Pembayaran,
    ): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun deletePembayaranByTermin(
        kavlingKode: String,
        termin: String,
    ): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun deleteAllPembayaran(kavlingKode: String): Flow<Result<Boolean>> {
        TODO("Not yet implemented")
    }

}