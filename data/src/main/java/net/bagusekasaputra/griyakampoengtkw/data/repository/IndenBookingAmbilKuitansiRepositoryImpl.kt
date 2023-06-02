package net.bagusekasaputra.griyakampoengtkw.data.repository

import net.bagusekasaputra.griyakampoengtkw.domain.entity.IndenBookingAmbilKuitansi
import net.bagusekasaputra.griyakampoengtkw.domain.repository.IndenBookingAmbilKuitansiRepository

class IndenBookingAmbilKuitansiRepositoryImpl: IndenBookingAmbilKuitansiRepository {

    override suspend fun get(keyId: String, termin: String): Result<IndenBookingAmbilKuitansi?> {
        TODO("Not yet implemented")
    }

    override suspend fun insert(ambilKuitansi: IndenBookingAmbilKuitansi): Result<Nothing?> {
        TODO("Not yet implemented")
    }
}