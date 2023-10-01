package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.delay
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoTambahanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoTambahanPembayaranRepository

class FotoTambahanPembayaranRepositoryImpl: FotoTambahanPembayaranRepository {
    override suspend fun get(kavling: String, id: String): Result<FotoTambahanPembayaran?> {
        return Result.failure(NotImplementedError("Operation not yet implemented"))
    }

    override suspend fun insert(entity: FotoTambahanPembayaran): Result<Nothing?> {
        delay(3000L)
        return Result.failure(NotImplementedError("Operation not yet implemented"))
    }

    override suspend fun isFotoExists(kavling: String, id: String): Result<Boolean> {
        return Result.failure(NotImplementedError("Operation not yet implemented"))
    }
}