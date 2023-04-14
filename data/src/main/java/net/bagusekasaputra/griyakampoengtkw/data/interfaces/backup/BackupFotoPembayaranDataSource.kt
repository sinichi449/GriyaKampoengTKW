package net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup

import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranModel

interface BackupFotoPembayaranDataSource {

    suspend fun getFotoPembayaran(kavlingKode: String, termin: String): Result<FotoPembayaranModel?>

    suspend fun isFotoPembayaranExist(kavlingKode: String, termin: String): Result<Boolean?>

    suspend fun createBackup(backupPath: String, listFotoPembayaran: List<FotoPembayaranModel>): Result<Nothing?>

}