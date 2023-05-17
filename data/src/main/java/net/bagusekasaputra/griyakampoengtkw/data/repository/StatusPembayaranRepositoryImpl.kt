package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.entity.statusPembayaran.StatusPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.StatusPembayaranRepository

class StatusPembayaranRepositoryImpl: StatusPembayaranRepository {

    private val d1 = StatusPembayaran("D1", listOf(
        StatusPembayaran.Nil("3/4/2023".toDate()),
        StatusPembayaran.Aktif("15/4/2023".toDate()),
    ))

    private val mapStatusPembayaran = mapOf(
        Pair("D1", d1)
    )

    override fun get(kavling: String, dataMode: DataMode): Flow<Result<StatusPembayaran?>> {
        return flow {
            delay(3000L)

            emit(Result.success(mapStatusPembayaran[kavling]))
        }
    }
}