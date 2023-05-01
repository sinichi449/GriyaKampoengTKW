package net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup

import net.bagusekasaputra.griyakampoengtkw.data.model.CatatanPembayaranModel

interface BackupCatatanPembayaranDataSource {

    suspend fun getCatatanPembayaran(kavlingKode: String): Result<CatatanPembayaranModel?>

    suspend fun createBackup(backupPath: String, listCatatanPembayaran: List<CatatanPembayaranModel>): Result<Nothing?>

}