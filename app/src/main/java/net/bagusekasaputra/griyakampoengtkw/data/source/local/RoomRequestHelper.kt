package net.bagusekasaputra.griyakampoengtkw.data.source.local

object RoomRequestHelper {

    fun <O> doGetOperation(producingWork: () -> O?): Result<O?> {
        return try {
            val result  = producingWork()

            return Result.success(result)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    fun doNonGetOperation(whatKindOfWork: () -> Unit): Result<Nothing?> {
        return try {
            whatKindOfWork()

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }
}