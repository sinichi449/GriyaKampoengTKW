package net.bagusekasaputra.griyakampoeng.tkw.data.local

import android.util.Log

object RoomRequestHelper {

    inline fun <M> roomOperation(work: () -> M): Result<M> {
        return try {
            Result.success(work())
        } catch (e: java.lang.Exception) {
            Log.d("INDEN_BOOKING", "Exception in Room Database: " +
                    "${e.javaClass.simpleName}:${e.message}")

            Result.failure(e)
        }
    }

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

    fun <O, T> doInsertPreventDuplicateOperation(
        outerData: O,
        targetData: T?,
        equalityPredicate: (O, T) -> Boolean,
        insertWork: (toBeInserted: O) -> Unit
    ): Result<Nothing?> {
        return try {
            if (targetData == null) {
                insertWork(outerData)
            } else {
                val isIdentical = equalityPredicate(outerData, targetData)

                if (isIdentical.not())
                    insertWork(outerData)
            }

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }
}