package net.bagusekasaputra.griyakampoengtkw.domain.entity


data class BackupRestoreEntity(
    val backupName: String,
    val listBlok: List<Block>,
    val listKavling: HashMap<String, List<Kavling>>,
)