package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.IndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranRepository

class GetAllIndenBookingAsyncUseCase(
    private val indenBookingRepository: IndenBookingRepository,
    private val dataDiriRepository: DataDiriRepository,
    private val pembayaranRepository: PembayaranRepository,
): AsyncUseCase<GetAllIndenBookingAsyncUseCase.Request, List<IndenBooking>>() {

    data class Request(val dataMode: DataMode): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<IndenBooking>?>> {
        return flow {
            val indenBookingList = mutableListOf<IndenBooking>()

            val keyIds = indenBookingRepository.getAllKeyIds(request.dataMode).getOrThrow()
            keyIds?.forEach { keyId ->
                Log.d("INDEN_BOOKING", "Found KEY_IDS! : $keyId")

                val dataDiri = dataDiriRepository.getFromIndenBooking(keyId)
                    .getOrThrow()
                val pembayarans = pembayaranRepository.getAllFromIndenBooking(keyId)
                    .getOrThrow()
                val fotoIdentitas = indenBookingRepository.getFotoIdentitas(keyId, request.dataMode)
                    .getOrThrow()

                val namaCostumer = dataDiri?.nama ?: "NULL"
                val noIdentitas = dataDiri?.noIdentitas ?: "0000"
                val uangMasuk = pembayarans?.let {
                    Pembayaran.hitungTotalUangMasuk(it)
                } ?: 0L

                indenBookingList.add(IndenBooking(
                    namaCostumer = namaCostumer,
                    noIdentitas = noIdentitas,
                    totalUangMasuk = uangMasuk,
                    fotoIdentitas = fotoIdentitas,
                    keyId = keyId,
                ))
            }

            emit(Result.success(indenBookingList))
        }
    }
}