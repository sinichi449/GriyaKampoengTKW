package net.bagusekasaputra.griyakampoengtkw.domain.mockRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BiayaMarketingRepository

class MockBiayaMarketingRepository: BiayaMarketingRepository {

    private val mapBiayaMarketing = mutableMapOf<String, List<BiayaMarketing>?>(
        Pair("A2", listOf(
            BiayaMarketing(kavlingKode = "A2", tanggal = "18/11/2022", jenisBiaya = "Pembuatan Surat Perjanjian", harga = "50,000"),
        )),
        Pair("A4", listOf(
            BiayaMarketing(kavlingKode = "A4", tanggal = "18/11/2022", jenisBiaya = "Biaya pembuatan surat", harga = "50,000"),
            BiayaMarketing(kavlingKode = "A4", tanggal = "18/11/2022", jenisBiaya = "Biaya pengiriman Surat ke Jakarta", harga = "20,000"),
        )),
        Pair("A9", listOf(
            BiayaMarketing(kavlingKode = "A9", tanggal = "18/11/2022", jenisBiaya = "biaya pembuatan surat", harga = "50,000"),
        )),
        Pair("A14", listOf(
            BiayaMarketing(kavlingKode = "A14", tanggal = "22/11/2022", jenisBiaya = "Biaya pembuatan surat", harga = "50,000"),
            BiayaMarketing(kavlingKode = "A14", tanggal = "22/11/2022", jenisBiaya = "biaya antar jemput ibunya B lancy", harga = "50,000"),
        )),
        Pair("B12", listOf(
            BiayaMarketing(kavlingKode = "B12", tanggal = "14/12/2022", jenisBiaya = "pembuatan surat", harga = "50,000"),
            BiayaMarketing(kavlingKode = "B12", tanggal = "15/12/2022", jenisBiaya = "pengiriman surat", harga = "50,000"),
        )),
        Pair("B18", listOf(
            BiayaMarketing(kavlingKode = "B18", tanggal = "22/12/2022", jenisBiaya = "pembuatan surat", harga = "50,000"),
        )),
    )

    override fun getBatchOnline(listKavling: List<String>): Flow<Result<Map<String, List<BiayaMarketing>?>?>> {
        return flow {
            val result = mutableMapOf<String, List<BiayaMarketing>?>()

            listKavling.forEach { kavling ->
                result[kavling] = mapBiayaMarketing[kavling]
            }

            emit(Result.success(result))
        }
    }

    override fun getAllByKavlingKode(
        kavlingKode: String,
        offline: Boolean,
    ): Flow<Result<List<BiayaMarketing>?>> {
        return flow {
            emit(Result.success(mapBiayaMarketing[kavlingKode]))
        }
    }

    override fun getAllOnline(kavlingKode: String): Flow<Result<List<BiayaMarketing>?>> {
        TODO("Not yet implemented")
    }

    override fun addBiayaMarketing(biayaMarketing: BiayaMarketing): Flow<Result<Nothing?>> {
        TODO("Not yet implemented")
    }

    override fun update(
        oldBiayaMarketing: BiayaMarketing,
        newBiayaMarketing: BiayaMarketing,
    ): Flow<Result<Nothing?>> {
        TODO("Not yet implemented")
    }

    override fun deleteSingle(
        kavlingKode: String,
        biayaMarketing: BiayaMarketing,
    ): Flow<Result<Nothing?>> {
        TODO("Not yet implemented")
    }

    override fun deleteAll(kavlingKode: String): Flow<Result<Nothing?>> {
        TODO("Not yet implemented")
    }
}