package net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup

import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel

interface BackupPembayaranDataSource {

    suspend fun getAllPembayaran(kavlingKode: String): Result<List<PembayaranModel>?>

}