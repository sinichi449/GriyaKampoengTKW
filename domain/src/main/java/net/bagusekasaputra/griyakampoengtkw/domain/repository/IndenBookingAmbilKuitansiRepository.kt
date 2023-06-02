package net.bagusekasaputra.griyakampoengtkw.domain.repository

import net.bagusekasaputra.griyakampoengtkw.domain.entity.IndenBookingAmbilKuitansi

interface IndenBookingAmbilKuitansiRepository {

    suspend fun get(keyId: String, termin: String): Result<IndenBookingAmbilKuitansi?>

    suspend fun insert(ambilKuitansi: IndenBookingAmbilKuitansi): Result<Nothing?>
}