package net.bagusekasaputra.griyakampoengtkw.domain.model

import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.BulanAngsuran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran

interface JsonModel<T> {

    fun toDomain(args: Any? = null): T

}

object TestingDataNodes {
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
    const val MAINTENTANCE = "maintenance"
}

data class PembayaranJson(
    val jumlahUangDibayar: Long,
    val keterangan: String,
    val tanggal: String,
    val termin: String,
    val timeMillis: Long,
    val urutan: Int,
    val invoiceDateStr: String?,
): JsonModel<Pembayaran> {

    override fun toDomain(args: Any?): Pembayaran {
        val bulanAngsuran = if (invoiceDateStr.isNullOrEmpty()) {
            BulanAngsuran.defaultToTanggalPembayaran(tanggal)
        } else {
            BulanAngsuran.fromString(invoiceDateStr, "/")
        }

        return Pembayaran(
            termin = "$termin $urutan",
            tanggal = tanggal,
            jumlahUangDibayar = NumberUtil.formatLongToString(jumlahUangDibayar),
            keterangan = keterangan,
            timeMillis = timeMillis,
            bulanAngsuran = bulanAngsuran,
        )
    }

}

data class DataDiriJson(
    val alamatIndo: String,
    val alamatKerja: String,
    val jenisIdentitas: String,
    val nama: String,
    val negaraBekerja: String,
    val noHp: String,
    val noIdentitas: String,
): JsonModel<DataDiri> {

    override fun toDomain(args: Any?): DataDiri {
        return DataDiri(
            nama, jenisIdentitas, noIdentitas, negaraBekerja,
            alamatKerja, alamatIndo, noHp
        )
    }

}

data class HargaKavlingJson(
    val harga: Long,
    val kavlingKode: String,
    val tambahLuasan: Long,
): JsonModel<HargaKavling> {

    override fun toDomain(args: Any?): HargaKavling {
        return HargaKavling(
            kavlingKode,
            NumberUtil.formatLongToString(harga),
            NumberUtil.formatLongToString(tambahLuasan),
        )
    }
}

data class FeeMarketingJson(
    val biayaMarketer: Long,
    val kavlingKode: String,
    val namaMarketer: String,
    val tanggalStr: String,
    val timeMillis: Long,
): JsonModel<FeeMarketing> {
    override fun toDomain(args: Any?): FeeMarketing {
        return FeeMarketing(
            kavlingKode, namaMarketer,
            NumberUtil.formatLongToString(biayaMarketer), tanggalStr,
        )
    }
}

data class BiayaMarketingJson(
    val harga: Long,
    val jenisBiaya: String,
    val kavlingKode: String,
    val tanggal: String,
): JsonModel<BiayaMarketing> {
    override fun toDomain(args: Any?): BiayaMarketing {
        return BiayaMarketing(
            kavlingKode = kavlingKode,
            tanggal = tanggal,
            jenisBiaya = jenisBiaya,
            harga = NumberUtil.formatLongToString(harga),
        )
    }
}

data class BiayaLainJson(
    val harga: Long,
    val jenisBiaya: String,
    val tanggal: String,
): JsonModel<BiayaLain> {
    override fun toDomain(args: Any?): BiayaLain {
        return BiayaLain(
            jenisBiaya = jenisBiaya,
            harga = harga,
            tanggal = tanggal,
        )
    }
}

data class BaselinePembayaranJson(
    val jumlahUang: Long,
    val kavling: String,
    val opsiBulan: Int,
    val tanggalPembayaranMaks: Int,
    val timeMillis: Long,
): JsonModel<BaselinePembayaran> {
    override fun toDomain(args: Any?): BaselinePembayaran {
        return BaselinePembayaran(
            kavling, opsiBulan,
            jumlahUang, tanggalPembayaranMaks
        )
    }
}

