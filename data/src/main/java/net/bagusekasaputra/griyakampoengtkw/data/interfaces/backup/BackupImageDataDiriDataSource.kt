package net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup

import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriModel

interface BackupImageDataDiriDataSource {

    suspend fun getImageDataDiri(kavlingKode: String): Result<ImageDataDiriModel?>

    suspend fun createBackup(backupPath: String, listImageDataDiri: List<ImageDataDiriModel>?): Result<Nothing?>

}