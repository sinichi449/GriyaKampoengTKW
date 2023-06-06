package net.bagusekasaputra.griyakampoengtkw.domain.repository

import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.HargaRumahIndenBooking

@Deprecated("Migrated to \"testing_data.json\" with Mockito Library.")
class MockHargaRumahIndenBookingRepository: HargaRumahIndenBookingRepository {

    private val mapHargaRumah = mapOf(
        "3053d174-4b9b-437c-96aa-68fd44fa0fef" to HargaRumahIndenBooking(230_000_000L, 50_000_000L, "3053d174-4b9b-437c-96aa-68fd44fa0fef"),
    )

    override suspend fun get(keyId: String, dataMode: DataMode): Result<HargaRumahIndenBooking?> {
        return Result.success(mapHargaRumah[keyId])
    }

    override suspend fun update(
        keyId: String,
        newHargaRumah: HargaRumahIndenBooking
    ): Result<Nothing?> {
        TODO("Not yet implemented")
    }
}