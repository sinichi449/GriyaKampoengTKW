package net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup

import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingCatatanPembayaranModel

interface BackupCatatanPembayaranDataSource {

    suspend fun getCatatanPembayaran(kavlingKode: String): Result<KavlingCatatanPembayaranModel?>

    suspend fun createBackup(backupPath: String, listCatatanPembayaran: List<KavlingCatatanPembayaranModel>): Result<Nothing?>

}