package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteStatusPembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.entity.statusPembayaran.LogPengembalian
import net.bagusekasaputra.griyakampoengtkw.domain.entity.statusPembayaran.StatusPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.StatusPembayaranRepository

class StatusPembayaranRepositoryImpl(
    private val remoteDataSource: RemoteStatusPembayaranDataSource,
): StatusPembayaranRepository {

    private val d1 = StatusPembayaran("D1", listOf(
        listOf(
            StatusPembayaran.Nil("3/4/2023".toDate(), "Pembukaan kavling Mas Andik 3 April 2023"),
            StatusPembayaran.Aktif("15/4/2023".toDate(), "Pembayaran ITJ pertama"),
            StatusPembayaran.Suspend("18/08/2023".toDate(), "Ada kendala angsuran karena blablabla"),
            StatusPembayaran.Jeda("30/09/2023".toDate(), "Secara sepihak membatalkan, tetapi User bersedia mencari pengganti agar proses pengembalian uang DP bisa dilakukan."),
            StatusPembayaran.Batal(
                tanggalBatal = "12/12/2023".toDate(),
                riwayatTotalUangMasuk = 70_000_000L,
                logPengembalians = listOf(
                    LogPengembalian("D1", "14/12/2023".toDate(), 3_000_000L),
                    LogPengembalian("D1", "04/01/2024".toDate(), 3_000_000L),
                    LogPengembalian("D1", "09/02/2024".toDate(), 3_000_000L),
                )
            )
        ),
        listOf(
            StatusPembayaran.Nil("13/12/2023".toDate(), "Kavling dibuka kembali"),
        )
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