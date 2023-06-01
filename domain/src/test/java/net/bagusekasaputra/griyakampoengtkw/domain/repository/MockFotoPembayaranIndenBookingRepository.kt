package net.bagusekasaputra.griyakampoengtkw.domain.repository

import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.FotoPembayaranIndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.repository.indenBooking.FotoPembayaranIndenBookingRepository

class MockFotoPembayaranIndenBookingRepository: FotoPembayaranIndenBookingRepository {

    private val mapForIndenBooking = mutableMapOf(
        "3053d174-4b9b-437c-96aa-68fd44fa0fef" to listOf(
            FotoPembayaranIndenBooking("3053d174-4b9b-437c-96aa-68fd44fa0fef", "ITJ 1", ""),
            FotoPembayaranIndenBooking("3053d174-4b9b-437c-96aa-68fd44fa0fef", "DP 1", ""),
            FotoPembayaranIndenBooking("3053d174-4b9b-437c-96aa-68fd44fa0fef", "DP 2", ""),
        )
    )

    override suspend fun get(keyId: String, termin: String): Result<FotoPembayaranIndenBooking?> {
        val fotoPembayaran = mapForIndenBooking[keyId]?.filter {
            it.termin == termin
        }?.get(0)

        return Result.success(fotoPembayaran)
    }

    override suspend fun insert(fotoPembayaran: FotoPembayaranIndenBooking): Result<Nothing?> {
        TODO("Not yet implemented")
    }

    override suspend fun isExist(keyId: String, termin: String): Result<Boolean> {
        val fotoPembayaran = mapForIndenBooking[keyId]?.filter {
            it.termin == termin
        }?.get(0)

        return if (fotoPembayaran != null) {
            Result.success(true)
        } else {
            Result.success(false)
        }
    }

}