package net.bagusekasaputra.griyakampoengtkw.domain.entity


data class BackupRestoreEntity(
    val backupName: String,
    val listBlok: List<Block>,
    val listKavling: HashMap<String, List<Kavling>>,
    val listPembayaran: Map<String, List<Pembayaran>?>,
    val listDataDiri: Map<String, DataDiri?>,
)