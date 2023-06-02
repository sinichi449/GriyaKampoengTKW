package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.ImageDataDiriUri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.ImageSprUri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.catatanPembayaran.KavlingCatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran


data class BackupRestoreEntity(
    val backupName: String,
    val backupSavePath: String,
    val listBlok: List<Block>,
    val listKavling: HashMap<String, List<Kavling>>,
    val listPembayaran: Map<String, List<Pembayaran>?>,
    val listDataDiri: Map<String, DataDiri?>,
    val listHargaKavling: List<HargaKavling>,
    val listKavlingCatatanPembayaran: List<KavlingCatatanPembayaran>,
    val listBiayaMarketing: List<BiayaMarketing>,
    val listFeeMarketing: List<FeeMarketing>,
    val listBiayaLain: List<BiayaLain>,
    val listImageDataDiriUri: List<ImageDataDiriUri>,
    val listFotoPembayaran: List<FotoPembayaran>,
    val listImageSprUri: List<ImageSprUri>,
)