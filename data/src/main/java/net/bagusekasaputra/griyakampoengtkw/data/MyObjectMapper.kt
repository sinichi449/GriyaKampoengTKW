package net.bagusekasaputra.griyakampoengtkw.data

import android.content.ContentResolver
import android.net.Uri
import net.bagusekasaputra.griyakampoengtkw.data.model.AppUpdateModel
import net.bagusekasaputra.griyakampoengtkw.data.model.BaselinePembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaLainModel
import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaMarketingModel
import net.bagusekasaputra.griyakampoengtkw.data.model.BlockModel
import net.bagusekasaputra.griyakampoengtkw.data.model.CatatanPembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel
import net.bagusekasaputra.griyakampoengtkw.data.model.FeeMarketingModel
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaKavlingModel
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriModel
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageSprModel
import net.bagusekasaputra.griyakampoengtkw.data.model.IndenBookingModel
import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingModel
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toSlashedString
import net.bagusekasaputra.griyakampoengtkw.domain.ImageUtil
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.AppUpdate
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.entity.CatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.ImageSpr
import net.bagusekasaputra.griyakampoengtkw.domain.entity.IndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.ImageDataDiriUri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.ImageSprUri


/**
 * Collections of Mapping Function
 * Domain -> DataModel object
 * and vice versa.
 */
object MyObjectMapper {

    /**
     * App Update
     */
    fun mapAppUpdate(appUpdateModel: AppUpdateModel): AppUpdate {
        return appUpdateModel.let {
            AppUpdate(
                latestVersion = it.latestVersion,
                latestVersionCode = it.latestVersionCode,
                url = it.url,
                releaseNotes = it.releaseNotes,
            )
        }
    }


    /**
     * Biaya Lain
     */
    fun mapBiayaLain(model: BiayaLainModel): BiayaLain {
        return model.let {
            BiayaLain(
                jenisBiaya = it.jenisBiaya,
                harga = it.harga,
                tanggal = it.tanggal,
            )
        }
    }

    fun mapBiayaLain(biayaLain: BiayaLain): BiayaLainModel {
        return biayaLain.let {
            BiayaLainModel(
                jenisBiaya = it.jenisBiaya,
                harga = it.harga,
                tanggal = it.tanggal,
            )
        }
    }


    /**
     * Biaya Marketing
     */
    fun mapBiayaMarketing(biayaMarketing: BiayaMarketing): BiayaMarketingModel {
        return biayaMarketing.let {
            BiayaMarketingModel(
                tanggal = it.tanggal,
                kavlingKode = it.kavlingKode,
                jenisBiaya = it.jenisBiaya,
                harga = NumberUtil.formatStringToLong(it.harga), // from UI layer, the harga is formatted into comma separated
            )
        }
    }

    fun mapBiayaMarketing(biayaMarketingModel: BiayaMarketingModel): BiayaMarketing {
        return biayaMarketingModel.let {
            BiayaMarketing(
                kavlingKode = it.kavlingKode,
                tanggal = it.tanggal,
                jenisBiaya = it.jenisBiaya,
                harga = it.harga.toString(),
            )
        }
    }


    /**
     * Block
     */
    fun mapBlockModel(blockModel: BlockModel): Block {
        return blockModel.let {
            Block(
                kode = it.kode,
                warna = it.warna,
            )
        }
    }

    fun mapBlockModel(block: Block): BlockModel {
        return block.let {
            BlockModel(
                kode = it.kode,
                warna = it.warna,
            )
        }
    }


    /**
     * Catatan Pembayaran
     */
    fun mapCatatanPembayaran(catatanPembayaranModel: CatatanPembayaranModel): CatatanPembayaran {
        return catatanPembayaranModel.let {
            CatatanPembayaran(
                kavlingKode = it.kavlingKode,
                content = it.content,
            )
        }
    }

    fun mapCatatanPembayaran(catatanPembayaran: CatatanPembayaran): CatatanPembayaranModel {
        return catatanPembayaran.let {
            CatatanPembayaranModel(
                kavlingKode = it.kavlingKode,
                content = it.content,
            )
        }
    }


    /**
     * Data Diri
     */
    fun mapDataDiri(dataDiriModel: DataDiriModel): DataDiri {
        return dataDiriModel.let {
            DataDiri(
                nama = it.nama,
                jenisIdentitas = it.jenisIdentitas,
                noIdentitas = it.noIdentitas,
                negaraBekerja = it.negaraBekerja,
                alamatIndo = it.alamatIndo,
                alamatKerja = it.alamatKerja,
                noHp = it.noHp
            )
        }
    }

    fun mapDataDiri(dataDiri: DataDiri): DataDiriModel {
        return dataDiri.let {
            DataDiriModel(
                nama = it.nama,
                jenisIdentitas = it.jenisIdentitas,
                noIdentitas = it.noIdentitas,
                alamatKerja = it.alamatKerja,
                negaraBekerja = it.negaraBekerja,
                alamatIndo = it.alamatIndo,
                noHp = it.noHp
            )
        }
    }


    /**
     * Fee Marketing
     */
    fun mapFeeMarketing(feeMarketingModel: FeeMarketingModel): FeeMarketing =
        feeMarketingModel.let {
            FeeMarketing(
                kavlingKode = it.kavlingKode,
                namaMarketer = it.namaMarketer,
                biayaMarketer = it.biayaMarketer.toString(),
                tanggalPenerimaan = it.getTanggalStr(),
            )
        }

    fun mapFeeMarketing(feeMarketing: FeeMarketing) =
        feeMarketing.let {
            FeeMarketingModel(
                timeMillis = it.getTimemillisTanggalPenerimaan(),
                kavlingKode = it.kavlingKode,
                namaMarketer = it.namaMarketer,
                biayaMarketer = it.biayaMarketer.toLong(),
            )
        }


    /**
     * Harga Kavling
     */
    fun mapHargaKavling(hargaKavling: HargaKavling): HargaKavlingModel {
        return hargaKavling.let {
            HargaKavlingModel(
                kavlingKode = it.kavlingKode,
                harga = NumberUtil.formatStringToLong(it.harga),
                tambahLuasan = NumberUtil.formatStringToLong(it.tambahanLuas),
            )
        }
    }

    fun mapHargaKavling(hargaKavlingModel: HargaKavlingModel): HargaKavling {
        return hargaKavlingModel.let {
            HargaKavling(
                kavlingKode = it.kavlingKode,
                harga = NumberUtil.formatLongToString(it.harga),
                tambahanLuas = NumberUtil.formatLongToString(it.tambahLuasan),
            )
        }
    }


    /**
     *  Kavling
     */
    fun mapKavling(kavlingModel: KavlingModel): Kavling {
        return Kavling(
            kavlingModel.kode,
            kavlingModel.active,
            kavlingModel.warna,
            kavlingModel.ukuran,
            kavlingModel.type
        )
    }

    fun mapKavling(kavling: Kavling): KavlingModel {
        return KavlingModel(
            kavling.kode,
            kavling.warna,
            kavling.belumIsi,
            kavling.ukuran,
            kavling.type
        )
    }


    /**
     * Pembayaran
     */
    fun mapPembayaran(pembayaran: Pembayaran): PembayaranModel {
        return pembayaran.let {
            val pisah = PembayaranModel.pisahkanTerminDanUrutan(it.termin)

            return@let PembayaranModel(
                termin = pisah["jenis"]!!,
                urutan = pisah["urutan"]!!.toInt(),
                tanggal = it.tanggal,
                jumlahUangDibayar = NumberUtil.formatStringToLong(it.jumlahUangDibayar),
                keterangan = it.keterangan,
                timeMillis = it.timeMillis,
            )
        }
    }

    fun mapPembayaran(pembayaranModel: PembayaranModel): Pembayaran {
        return pembayaranModel.let {
            Pembayaran(
                termin = "${it.termin} ${it.urutan}",
                tanggal = it.tanggal,
                jumlahUangDibayar = NumberUtil.formatLongToString(it.jumlahUangDibayar),
                keterangan = it.keterangan,
                timeMillis = it.timeMillis,
            )
        }
    }


    /**
     * Image Data Diri
     */
    fun mapImageDataDiri(imageDataDiriUri: ImageDataDiriUri): ImageDataDiriModel {
        return imageDataDiriUri.let {
            ImageDataDiriModel(
                kavlingKode = it.kavling,
                imgUri = it.uriStr,
            )
        }
    }


    /**
     * Foto Pembayaran
     */
    fun mapFotoPembayaran(fotoPembayaran: FotoPembayaran): FotoPembayaranModel {
        return fotoPembayaran.let {
            FotoPembayaranModel(
                id = it.id,
                kavlingKode = it.kavlingKode,
                termin = it.termin,
                uriStr = it.uri.toString(),
            )
        }
    }

    fun mapFotoPembayaran(fotoPembayaranModel: FotoPembayaranModel): FotoPembayaran {
        return fotoPembayaranModel.let {
            FotoPembayaran(
                id = it.id,
                kavlingKode = it.kavlingKode,
                termin = it.termin,
                uri = it.getUri(),
            )
        }
    }


    /**
     * Image Spr
     */
    fun mapImageSpr(imageSprModel: ImageSprModel): ImageSprUri {
        return imageSprModel.let {
            ImageSprUri(
                kavlingKode = it.kavlingKode,
                uriStr = it.dstUri,
            )
        }
    }

    fun mapImageSpr(imageSprUri: ImageSprUri): ImageSprModel {
        return imageSprUri.let {
            ImageSprModel(
                kavlingKode = it.kavlingKode,
                dstUri = it.uriStr,
            )
        }
    }

    fun mapImageSpr(imageSpr: ImageSpr, dstUri: String): ImageSprModel {
        return imageSpr.let {
            ImageSprModel(
                kavlingKode = it.kavlingKode,
                dstUri = dstUri,
            )
        }
    }

    fun mapImageSpr(imageSprModel: ImageSprModel, contentResolver: ContentResolver): ImageSpr {
        return imageSprModel.let {
            ImageSpr(
                kavlingKode = it.kavlingKode,
                bitmap = ImageUtil.getBitmapFromUri(contentResolver, Uri.parse(it.dstUri))
            )
        }
    }


    /**
     * Baseline Pembayaran
     */
    fun mapBaselinePembayaran(model: BaselinePembayaranModel): BaselinePembayaran {
        return model.let {
            BaselinePembayaran(
                kavling = it.kavling,
                jumlahUang = it.jumlahUang,
                timeMillis = it.timeMillis,
            )
        }
    }

    fun mapBaselinePembayaran(baselinePembayaran: BaselinePembayaran): BaselinePembayaranModel {
        return baselinePembayaran.let {
            BaselinePembayaranModel(
                kavling = it.kavling,
                jumlahUang = it.jumlahUang,
                timeMillis = it.timeMillis,
            )
        }
    }


    /**
     * Inden Booking
     */
    fun mapIndenBooking(model: IndenBookingModel): IndenBooking {
        return model.let {
            IndenBooking(
                namaCostumer = it.namaCostumer,
                tanggalDibayar = it.tanggalDibayar.toDate(),
                jumlahUang = it.jumlahUang,
                noHp = it.noHp,
                keterangan = it.keterangan,
                timeMillis = it.timeMillis,
            )
        }
    }

    fun mapIndenBooking(indenBooking: IndenBooking): IndenBookingModel {
        return indenBooking.let {
            IndenBookingModel(
                timeMillis = it.timeMillis,
                namaCostumer = it.namaCostumer,
                tanggalDibayar = it.tanggalDibayar.toSlashedString(),
                jumlahUang = it.jumlahUang,
                noHp = it.noHp,
                keterangan = it.keterangan,
            )
        }
    }
}