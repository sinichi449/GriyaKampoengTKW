package net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup

import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranModel

interface BackupFotoPembayaranDataSource {

    fun getFotoPembayaran(kavlingKode: String, termin: String): Result<FotoPembayaranModel?>

    fun isFotoPembayaranExist(kavlingKode: String, termin: String): Result<Boolean?>

    fun createBackup(backupPath: String, listFotoPembayaran: List<FotoPembayaranModel>): Result<Nothing?>

}