package net.bagusekasaputra.griyakampoengtkw.domain.entity

data class BackupRestoreEntity(
    val title: String,
    val listBiayaLain: List<BiayaLain>,
    val listBiayaMarketing: List<BiayaMarketing>,
    val listBlok: List<Block>,
    val listCatatanPembayaran: List<CatatanPembayaran>,
    val listDataDiri: List<DataDiri>,
    val listFeeMarketing: List<FeeMarketing>,
    val listFormPembayaran: List<Pembayaran>,
    val listHargaKavling: List<HargaKavling>,
    val listKavling: List<Kavling>,
)