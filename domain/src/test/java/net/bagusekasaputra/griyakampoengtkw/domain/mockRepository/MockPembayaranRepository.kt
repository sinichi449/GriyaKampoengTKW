package net.bagusekasaputra.griyakampoengtkw.domain.mockRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository

class MockPembayaranRepository: PembayaranRepository {

    private val timeMillis = System.currentTimeMillis()
    private val mapPembayaran = mapOf<String, List<Pembayaran>?>(
        Pair("A2", listOf(
            Pembayaran("DP 1", "31/10/2022", "6,600,000", "", 0.0, "", "-", timeMillis, true),
            Pembayaran("DP 2", "10/12/2022", "6,600,000", "", 0.0, "", "", timeMillis, true),
        )),
        Pair("A4", listOf(
            Pembayaran("DP 1", "16/10/2022", "40,000,000", "", 0.0, "", "", timeMillis, true),
            Pembayaran("Termin 1", "28/10/2022", "8,000,000", "", 0.0, "", "", timeMillis, true),
            Pembayaran("Termin 2", "01/12/2022", "8,000,000", "", 0.0, "", "", timeMillis, true),
            Pembayaran("Termin 3", "28/12/2022", "8,000,000", "", 0.0, "", "", timeMillis, true),
        )),
        Pair("A7", listOf(
            Pembayaran("ITJ 1", "12/11/2022", "1,000,000", "", 0.0, "", "", timeMillis, true),
            Pembayaran("ITJ 2", "05/12/2022", "4,000,000", "", 0.0, "", "", timeMillis, true),
        )),
        Pair("A9", listOf(
            Pembayaran("ITJ 1", "17/11/2022", "1,000,000", "", 0.0, "", "", timeMillis, true),
            Pembayaran("Termin 1", "08/12/2022", "7,000,000", "", 0.0, "", "", timeMillis, true),
        )),
        Pair("A14", listOf(
            Pembayaran("ITJ 1", "15/11/2022", "5,000,000", "", 0.0, "", "", timeMillis, true),
        )),
        Pair("B5", listOf(
            Pembayaran("ITJ 1", "09/12/2022", "1,000,000", "", 0.0, "", "", timeMillis, true),
        )),
        Pair("B6", listOf(
            Pembayaran("ITJ 1", "09/12/2022", "5,000,000", "", 0.0, "", "", timeMillis, true),
            Pembayaran("DP 1", "19/12/2022", "1,000,000", "", 0.0, "", "", timeMillis, true),
            Pembayaran("DP 2",  "19/12/2022", "1,375,000", "", 0.0, "", "", timeMillis, true),
            Pembayaran("DP 3", "21/12/2022", "5,000,000", "", 0.0, "", "", timeMillis, true),
            Pembayaran("DP 4", "25/12/2022", "13,000,000", "", 0.0, "", "", timeMillis, true),
        )),
        Pair("B11", listOf(
            Pembayaran("ITJ 1", "21/12/2022", "1,000,000", "", 0.0, "", "", timeMillis, true),
        )),
        Pair("B12", listOf(
            Pembayaran("ITJ 1", "12/12/2022", "6,500,000", "", 0.0, "", "", timeMillis, true),
        )),
        Pair("B16", listOf(
            Pembayaran("ITJ 1", "18/12/2022", "1,000,000", "", 0.0, "", "", timeMillis, true),
        )),
        Pair("B17", listOf(
            Pembayaran("ITJ 1", "13/12/2022", "1,000,000", "", 0.0, "", "", timeMillis, true),
        )),
        Pair("B18", listOf(
            Pembayaran("ITJ 1", "16/12/2022", "900,000", "", 0.0, "", "", timeMillis, true),
        ))
    )

    override fun getBatch(listKavling: List<String>): Flow<Result<Map<String, List<Pembayaran>?>?>> {
        return flow {
            val result = mutableMapOf<String, List<Pembayaran>?>()

            listKavling.forEach { noKavling ->
                result[noKavling] = mapPembayaran[noKavling]
            }

            emit(Result.success(result))
        }
    }

    override fun getAllPembayaran(
        kavlingKode: String,
        offline: Boolean,
    ): Flow<Result<List<Pembayaran>?>> {
        return flow {
            emit(Result.success(mapPembayaran[kavlingKode]))
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