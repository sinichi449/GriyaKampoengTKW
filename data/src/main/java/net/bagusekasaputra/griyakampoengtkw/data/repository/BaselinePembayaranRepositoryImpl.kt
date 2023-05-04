package net.bagusekasaputra.griyakampoengtkw.data.repository

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BaselinePembayaranRepository
import kotlin.random.Random

class BaselinePembayaranRepositoryImpl: BaselinePembayaranRepository {

    override fun get(kavling: String): Flow<Result<BaselinePembayaran?>> {
        return flow {
            delay(1000L)

            if (kavling == "D1") {
                emit(Result.success(BaselinePembayaran(kavling = "D1", jumlahUang = 6_900_000L,)))
            } else {
                emit(Result.success(null))
            }
        }
    }

    override fun insert(baselinePembayaran: BaselinePembayaran): Flow<Result<Nothing?>> {
        return flow {
            Log.d("DEBUG_ME", "BaselinePembayaran: Activated repository")
            delay(5000L)
            val isSuccess = Random.nextBoolean()

            if (isSuccess) emit(Result.success(null))
            else emit(Result.failure(Throwable("Unknown error occured!")))
        }
    }
}