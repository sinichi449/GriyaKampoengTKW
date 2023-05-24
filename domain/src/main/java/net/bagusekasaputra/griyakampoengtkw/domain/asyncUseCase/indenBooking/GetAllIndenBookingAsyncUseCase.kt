package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.IndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingRepository

class GetAllIndenBookingAsyncUseCase(
    private val indenBookingRepository: IndenBookingRepository,
): AsyncUseCase<GetAllIndenBookingAsyncUseCase.Request, List<IndenBooking>>() {

    data class Request(val dataMode: DataMode): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<IndenBooking>?>> {
        return flow {
            val indenBookings = mutableListOf<IndenBooking>()

            val keyIds = indenBookingRepository.getAllKeyIds(request.dataMode).getOrThrow()
            keyIds?.forEach { keyId ->
                Log.d("INDEN_BOOKING", "Found KEY_IDS! : $keyId")

                val dataDiri = indenBookingRepository.getDataDiri(keyId, request.dataMode)
                    .getOrThrow()
                val pembayarans = indenBookingRepository.getAllPembayaran(keyId, request.dataMode)
                    .getOrThrow()
                val fotoIdentitas = indenBookingRepository.getFotoIdentitas(keyId, request.dataMode)
                    .getOrThrow()

                val namaCostumer = dataDiri?.nama ?: "NULL"
                val noIdentitas = dataDiri?.noIdentitas ?: "0000"
                val uangMasuk = pembayarans?.let {
                    Pembayaran.hitungTotalUangMasuk(it)
                } ?: 0L

                indenBookings.add(IndenBooking(
                    namaCostumer = namaCostumer,
                    noIdentitas = noIdentitas,
                    totalUangMasuk = uangMasuk,
                    fotoIdentitas = fotoIdentitas,
                    keyId = keyId,
                ))
            }

            emit(Result.success(indenBookings))
        }
    }
}