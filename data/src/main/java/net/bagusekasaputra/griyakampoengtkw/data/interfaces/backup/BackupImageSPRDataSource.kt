package net.bagusekasaputra.griyakampoengtkw.data.interfaces.backup

import net.bagusekasaputra.griyakampoengtkw.data.model.ImageSprModel

interface BackupImageSPRDataSource {

    fun getImageSPR(kavlingKode: String): Result<ImageSprModel?>

    fun createBackup(backupPath: String, listSprImage: List<ImageSprModel>?): Result<Nothing?>

}