package net.bagusekasaputra.griyakampoengtkw.data.remote.baselinePembayaran

import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteBaselinePembayaranDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.BaselinePembayaranModel

class FirebaseBaselinePembayaranDataSource(

): RemoteBaselinePembayaranDataSource {

    override suspend fun get(kavling: String): Result<BaselinePembayaranModel?> {
        return if (kavling == "D1") {
            Result.success(
                BaselinePembayaranModel(
                    kavling = kavling,
                    jumlahUang = 6_750_000L,
                    timeMillis = System.currentTimeMillis(),
                )
            )
        } else {
            Result.success(null)
        }
    }

    override suspend fun insert(model: BaselinePembayaranModel): Result<Nothing?> {
        return Result.success(null)
    }

}

data class BaselinePembayaranRemoteModel(
    val kavling: String,
    val jumlahUang: Long,
    val timeMillis: Long,
)