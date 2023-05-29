package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.indenBooking.dataDiri

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository

class InsertDataDiriIndenBookingAsyncUseCase(
    private val dataDiriRepository: DataDiriRepository,
): AsyncUseCase<InsertDataDiriIndenBookingAsyncUseCase.Request, String>() {

    data class Request(val dataDiri: DataDiri): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<String?>> {
        return flow {
            val result = dataDiriRepository.insertFromIndenBooking(request.dataDiri)
            result.onSuccess { keyId ->
                Log.d("INDEN_BOOKING", "Add Data Diri ${request.dataDiri.nama} success with keyId [$keyId]")
                emit(Result.success(keyId))
            }
            result.onFailure {
                emit(Result.failure(it))
            }
        }
    }
}