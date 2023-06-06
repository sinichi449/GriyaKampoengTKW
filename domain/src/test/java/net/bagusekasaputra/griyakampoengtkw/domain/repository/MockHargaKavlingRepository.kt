package net.bagusekasaputra.griyakampoengtkw.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling

class MockHargaKavlingRepository: HargaKavlingRepository {

    private val mapHargaKavling = mutableMapOf(
        "A11" to HargaKavling(kavlingKode = "A11", harga = "230,000,000", tambahanLuas = "50,000,000")
    )

    override fun onlineBatch(listKavling: List<String>): Flow<Result<Map<String, HargaKavling?>?>> {
        return flow {
            val result = mutableMapOf<String, HargaKavling?>()
            listKavling.forEach {
                result[it] = mapHargaKavling[it]
            }

            emit(Result.success(result))
        }
    }

    override fun getBatchBackup(listKavling: List<String>): Flow<Result<Map<String, HargaKavling?>?>> {
        TODO("Not yet implemented")
    }

    override fun backupBatch(
        backupName: String,
        listKavling: List<String>
    ): Flow<Result<Map<String, HargaKavling?>?>> {
        TODO("Not yet implemented")
    }

    override fun getHargaKavling(
        kavlingKode: String,
        dataMode: DataMode
    ): Flow<Result<HargaKavling?>> {
        TODO("Not yet implemented")
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