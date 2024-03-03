package net.bagusekasaputra.griyakampoengtkw.data.remote

import com.google.firebase.database.DatabaseReference

object FirebaseNodes {
    const val BIAYA_MARKETING = "biayaMarketing"
    const val BLOCKS = "blocks"
    const val CATATAN_PEMBAYARAN = "catatanPembayaran"
    const val DATA_DIRI = "dataDiri"
    const val FEE_MARKETING = "feeMarketing"
    const val FORM_PEMBAYARAN = "formPembayaran"
    const val HARGA_KAVLING = "hargaKavling"
    const val KAVLINGS = "kavlings"
    const val UPDATE = "update"
    const val BIAYA_LAIN = "biayaLain"
    const val UNMIGRATED = "unmigrated"
    const val METADATA_ROOT = "metadata"
    const val BASELINE_PEMBAYARAN = "baselinePembayaran"
    const val INDEN_BOOKING = "indenBooking"
    const val HARGA_RUMAH = "hargaRumah"
    const val DATABASE_USER = "databaseUser"
    const val BACKUPS = "backups"
    const val STATUS_PEMBAYARAN = "statusPembayaran"
    const val PROMOTION = "promotion"
    const val AMBIL_KUITANSI = "ambilKuitansi"
    const val KAVLING_EXCLUSION_LIST = "excludeForRekap"
    const val PENGEMBALIAN_PEMBAYARAN = "pengembalianPembayaran"
    const val PEMBAYARAN_TAMBAH_LUASAN = "pembayaranTambahLuasan"
    const val MAINTENTANCE = "maintenance"

    const val IMAGE_DATA_DIRI = "data_diri_images"
    const val IMAGES_FOTO_PEMBAYARAN = "foto_pembayaran_images"
    const val IMAGE_SPR = "spr_images"
    const val IMAGE_INDEN_BOOKING = "inden_booking_images"
    const val IMAGE_PENGEMBALIAN = "pengembalian_images"

    const val OBJECT_NOT_FOUND_MESSAGE = "Object does not exist at location."

    fun getBackupNode(databaseReference: DatabaseReference, backupName: String, node: String): DatabaseReference {
        return databaseReference.child("$BACKUPS/$backupName/$node")
    }
}