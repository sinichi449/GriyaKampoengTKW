package net.bagusekasaputra.griyakampoengtkw.data

import android.content.ContentResolver
import android.net.Uri
import net.bagusekasaputra.griyakampoengtkw.data.model.AppUpdateModel
import net.bagusekasaputra.griyakampoengtkw.data.model.BaselinePembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaLainModel
import net.bagusekasaputra.griyakampoengtkw.data.model.BiayaMarketingModel
import net.bagusekasaputra.griyakampoengtkw.data.model.BlockModel
import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel
import net.bagusekasaputra.griyakampoengtkw.data.model.DatabaseUserModel
import net.bagusekasaputra.griyakampoengtkw.data.model.FeeMarketingModel
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranIndenBookingModel
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaKavlingModel
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaRumahModel
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriIndenBookingModel
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriModel
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageSprModel
import net.bagusekasaputra.griyakampoengtkw.data.model.IndenBookingAmbilKuitansiModel
import net.bagusekasaputra.griyakampoengtkw.data.model.IndenBookingCatatanPembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingCatatanPembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingModel
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel.Companion.toInvoiceDateStr
import net.bagusekasaputra.griyakampoengtkw.data.model.PengembalianModel
import net.bagusekasaputra.griyakampoengtkw.data.model.PromotionModel
import net.bagusekasaputra.griyakampoengtkw.data.model.StandardAmbilKuitansiModel
import net.bagusekasaputra.griyakampoengtkw.data.model.StatusPembayaranModel
import net.bagusekasaputra.griyakampoengtkw.data.model.StatusPembayaranModel.LogPengembalianModel
import net.bagusekasaputra.griyakampoengtkw.data.model.StatusPembayaranModel.LogStatusModel
import net.bagusekasaputra.griyakampoengtkw.data.model.TahapanModel
import net.bagusekasaputra.griyakampoengtkw.data.model.TambahanPembayaranModel
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toSlashedString
import net.bagusekasaputra.griyakampoengtkw.domain.ImageUtil
import net.bagusekasaputra.griyakampoengtkw.domain.NumberUtil
import net.bagusekasaputra.griyakampoengtkw.domain.entity.AppUpdate
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BaselinePembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Block
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DataDiri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.DatabaseUser
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FeeMarketing
import net.bagusekasaputra.griyakampoengtkw.domain.entity.FotoPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.IndenBookingAmbilKuitansi
import net.bagusekasaputra.griyakampoengtkw.domain.entity.IndenBookingCatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.KavlingCatatanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Promotion
import net.bagusekasaputra.griyakampoengtkw.domain.entity.StandardAmbilKuitansi
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Tahapan
import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.FotoPembayaranIndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.ImageDataDiriIndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.ImageDataDiriUri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.ImageSpr
import net.bagusekasaputra.griyakampoengtkw.domain.entity.images.ImageSprUri
import net.bagusekasaputra.griyakampoengtkw.domain.entity.indenBooking.HargaRumahIndenBooking
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.CombinedKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.Kavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling.StandardKavling
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.BulanAngsuran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.Pengembalian
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembayaran.TambahanPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.statusPembayaran.LogPengembalian
import net.bagusekasaputra.griyakampoengtkw.domain.entity.statusPembayaran.StatusPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.statusPembayaran.StatusPembayaran.LogStatus



/**
 * Collections of Mapping Function
 * Domain -> DataModel object
 * and vice versa.
 */
object MyObjectMapper {

    /**
     * Tahapan
     */
    fun mapTahapan(model: TahapanModel): Tahapan {
        return model.let {
            Tahapan(reference = it.reference)
        }
    }


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
     * Kavling Catatan Pembayaran
     */
    fun mapKavlingCatatanPembayaran(kavlingCatatanPembayaranModel: KavlingCatatanPembayaranModel): KavlingCatatanPembayaran {
        return kavlingCatatanPembayaranModel.let {
            KavlingCatatanPembayaran(
                kavlingKode = it.kavlingKode,
                mContent = it.content,
            )
        }
    }

    fun mapKavlingCatatanPembayaran(kavlingCatatanPembayaran: KavlingCatatanPembayaran): KavlingCatatanPembayaranModel {
        return kavlingCatatanPembayaran.let {
            KavlingCatatanPembayaranModel(
                kavlingKode = it.kavlingKode,
                content = it.content,
            )
        }
    }

    /**
     * Inden Booking Catatan Pembayaran
     */
    fun mapIndenBookingCatatanPembayaran(catatanPembayaran: IndenBookingCatatanPembayaran): IndenBookingCatatanPembayaranModel {
        return catatanPembayaran.let {
            IndenBookingCatatanPembayaranModel(
                keyId = it.keyId,
                content = it.content,
            )
        }
    }

    fun mapIndenBookingCatatanPembayaran(model: IndenBookingCatatanPembayaranModel): IndenBookingCatatanPembayaran {
        return model.let {
            IndenBookingCatatanPembayaran(
                keyId = it.keyId,
                mContent = it.content,
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
        return if (kavlingModel.isCombined) {
            CombinedKavling(
                kavlingKodeList = kavlingModel.getListKode(),
                belumIsi = kavlingModel.active,
                warna = kavlingModel.warna,
                ukuran = kavlingModel.ukuran,
                numKode = kavlingModel.getNumkode(),
                type = kavlingModel.type,
            )
        } else {
            StandardKavling(
                kavlingModel.kode,
                kavlingModel.active,
                kavlingModel.warna,
                kavlingModel.ukuran,
                kavlingModel.type
            )
        }
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
                invoiceDateStr = it.bulanAngsuran.toInvoiceDateStr(),
            )
        }
    }

    fun mapPembayaran(pembayaranModel: PembayaranModel): Pembayaran {
        return pembayaranModel.let {
            val bulanAngsuran = if (it.invoiceDateStr.isEmpty()) {
                    BulanAngsuran.defaultToTanggalPembayaran(it.tanggal)
                } else {
                    BulanAngsuran.fromString(it.invoiceDateStr, PembayaranModel.INVOICE_SEPARATOR)
                }

            Pembayaran(
                termin = "${it.termin} ${it.urutan}",
                tanggal = it.tanggal,
                jumlahUangDibayar = NumberUtil.formatLongToString(it.jumlahUangDibayar),
                keterangan = it.keterangan,
                timeMillis = it.timeMillis,
                bulanAngsuran = bulanAngsuran,
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
                opsiBulan = it.opsiBulan,
                jumlahUang = it.jumlahUang,
                tanggalPembayaranMaks = it.tanggalPembayaranMaks,
                timeMillis = it.timeMillis,
            )
        }
    }

    fun mapBaselinePembayaran(baselinePembayaran: BaselinePembayaran): BaselinePembayaranModel {
        return baselinePembayaran.let {
            BaselinePembayaranModel(
                kavling = it.kavling,
                opsiBulan = it.opsiBulan,
                jumlahUang = it.jumlahUang,
                tanggalPembayaranMaks = it.tanggalPembayaranMaks,
                timeMillis = it.timeMillis,
            )
        }
    }


    /**
     * Inden Booking
     */
    // TODO

    /**
     * Harga Rumah
     */
    fun mapHargaRumah(model: HargaRumahModel): HargaRumahIndenBooking {
        return model.let {
            HargaRumahIndenBooking(
                harga = it.harga,
                tambahLuasan = it.tambahLuasan,
                keyId = it.keyId,
            )
        }
    }

    fun mapHargaRumah(hargaRumah: HargaRumahIndenBooking): HargaRumahModel {
        return hargaRumah.let {
            HargaRumahModel(
                harga = it.harga,
                tambahLuasan = it.tambahLuasan,
                keyId = it.keyId,
            )
        }
    }


    /**
     * Database User
     */
    fun mapDatabaseUser(model: DatabaseUserModel): DatabaseUser {
        return model.let {
            DatabaseUser(
                nama = it.nama,
                tanggal = it.tanggal.toDate(),
                noHp = it.noHp,
                _usernameTiktok = it.usernameTiktok,
                lokasiIndo = it.lokasiIndo,
                negaraBekerja = it.negaraBekerja,
                keterangan = it.keterangan,
                lastModified = it.lastModified,
            )
        }
    }

    fun mapDatabaseUser(databaseUser: DatabaseUser): DatabaseUserModel {
        return databaseUser.let {
            DatabaseUserModel(
                nama = it.nama,
                tanggal = it.tanggal.toSlashedString(),
                noHp = it.noHp,
                usernameTiktok = it._usernameTiktok,
                lokasiIndo = it.lokasiIndo,
                negaraBekerja = it.negaraBekerja,
                keterangan = it.keterangan,
                lastModified = it.lastModified,
            )
        }
    }


    /**
     * Status Pembayaran
     */
    fun mapStatusPembayaran(model: StatusPembayaranModel): StatusPembayaran {
        return model.let {
            val listLogStatuses = mutableListOf<List<LogStatus>>()
            it.listLogStatuses.forEach { logStatusModels ->
                val logStatuses = mutableListOf<LogStatus>()

                logStatusModels.forEach { logStatusModel ->
                    when (logStatusModel.namaStatus) {
                        "Nil" -> {
                            logStatuses.add(StatusPembayaran.Nil(
                                tanggalDibuka = logStatusModel.tanggal.toDate(),
                                keteranganNil = logStatusModel.keterangan,
                                )
                            )
                        }
                        "Aktif" -> {
                            logStatuses.add(StatusPembayaran.Aktif(
                                tanggalItj = logStatusModel.tanggal.toDate(),
                                keteranganAktif = logStatusModel.keterangan,
                                )
                            )
                        }
                        "Suspend" -> {
                            logStatuses.add(StatusPembayaran.Suspend(
                                tanggalSuspend = logStatusModel.tanggal.toDate(),
                                keteranganSuspend = logStatusModel.keterangan
                                )
                            )
                        }
                        "Jeda" -> {
                            logStatuses.add(StatusPembayaran.Jeda(
                                tanggalJeda = logStatusModel.tanggal.toDate(),
                                keteranganJeda = logStatusModel.keterangan
                            )
                            )
                        }
                        "Batal" -> {
                            // Parsing Batal
                            val logsPengembalian = logStatusModel.logPengembalians
                                .map { logPengembalianModel ->
                                    LogPengembalian(
                                        kavling = logPengembalianModel.kavling,
                                        tanggal = logPengembalianModel.tanggal.toDate(),
                                        jumlahUangDikembalikan = logPengembalianModel.jumlahUangDikembalikan,
                                        keterangan = logPengembalianModel.keterangan,
                                    )
                                }

                            logStatuses.add(StatusPembayaran.Batal(
                                tanggalBatal = logStatusModel.tanggal.toDate(),
                                riwayatTotalUangMasuk = logStatusModel.riwayatTotalUangMasuk,
                                logPengembalians = logsPengembalian,
                            ))
                        }
                        else -> throw IllegalStateException("Tipe Log Status Pembayaran \"${logStatusModel.namaStatus}\" tidak diketahui!")
                    }
                }

                listLogStatuses.add(logStatuses)
            }

            StatusPembayaran(
                kavling = it.kavling,
                listLogStatuses = listLogStatuses,
            )
        }
    }

    fun mapStatusPembayaran(statusPembayaran: StatusPembayaran): StatusPembayaranModel {
        return statusPembayaran.let {
            val listLogStatusesModels = mutableListOf<List<LogStatusModel>>()
            it.listLogStatuses.forEach { logStatuses ->
                val logStatusModels = mutableListOf<LogStatusModel>()
                logStatuses.forEach { logStatus ->
                    val namaStatus = logStatus.getTitle()
                    val tanggal = logStatus.tanggal.toSlashedString()
                    val keterangan = logStatus.keterangan

                    if (logStatus is StatusPembayaran.Batal) {
                        val logPengembalianModels = logStatus.logPengembalians
                            .map { logPengembalian ->
                                LogPengembalianModel(
                                    kavling = logPengembalian.kavling,
                                    tanggal = logPengembalian.tanggal.toSlashedString(),
                                    jumlahUangDikembalikan = logPengembalian.jumlahUangDikembalikan,
                                    keterangan = logPengembalian.keterangan,
                                )
                            }

                        logStatusModels.add(LogStatusModel(
                            namaStatus = namaStatus,
                            tanggal = tanggal,
                            keterangan = keterangan,
                            riwayatTotalUangMasuk = logStatus.riwayatTotalUangMasuk,
                            logPengembalians = logPengembalianModels,
                        )
                        )
                    } else {
                        logStatusModels.add(LogStatusModel(
                            namaStatus = namaStatus,
                            tanggal = tanggal,
                            keterangan = keterangan,
                        ))
                    }
                }

                listLogStatusesModels.add(logStatusModels)
            }

            StatusPembayaranModel(
                kavling = it.kavling,
                listLogStatuses = listLogStatusesModels,
            )
        }
    }

    /**
     * Promotion
     */
    fun mapPromotion(model: PromotionModel): Promotion {
        return model.let {
            Promotion(
                title = it.title,
                texts = it.texts,
            )
        }
    }

    /**
     * Standard Ambil Kuitansi
     */
    fun mapStandardAmbilKuitansi(model: StandardAmbilKuitansiModel): StandardAmbilKuitansi {
        return model.let {
            StandardAmbilKuitansi(
                kavling = it.kavling,
                mTermin = it.termin,
                mSudahAmbil = it.sudahAmbil
            )
        }
    }

    fun mapStandardAmbilKuitansi(ambilKuitansi: StandardAmbilKuitansi): StandardAmbilKuitansiModel {
        return ambilKuitansi.let {
            StandardAmbilKuitansiModel(
                kavling = it.kavling,
                termin = it.termin,
                sudahAmbil = it.sudahAmbil,
            )
        }
    }

    /**
     *  Inden Booking Ambil Kuitansi
     */
    fun mapIndenBookingAmbilKuitansi(model: IndenBookingAmbilKuitansiModel): IndenBookingAmbilKuitansi {
        return model.let {
            IndenBookingAmbilKuitansi(
                keyId = it.keyId,
                mTermin = it.termin,
                mSudahAmbil = it.sudahAmbil,
            )
        }
    }

    fun mapIndenBookingAmbilKuitansi(ambilKuitansi: IndenBookingAmbilKuitansi): IndenBookingAmbilKuitansiModel {
        return ambilKuitansi.let {
            IndenBookingAmbilKuitansiModel(
                keyId = it.keyId,
                termin = it.termin,
                sudahAmbil = it.sudahAmbil,
            )
        }
    }


    /**
     * Foto Pembayaran Inden Booking
     */
    fun mapFotoPembayaranIndenBooking(model: FotoPembayaranIndenBookingModel?): FotoPembayaranIndenBooking? {
        return model?.let {
            FotoPembayaranIndenBooking(
                keyId = it.keyId,
                termin = it.termin,
                uriStrFotoPembayaran = it.uriStr
            )
        }
    }

    fun mapFotoPembayaranIndenBooking(fotoPembayaran: FotoPembayaranIndenBooking): FotoPembayaranIndenBookingModel {
        return fotoPembayaran.let {
            FotoPembayaranIndenBookingModel(
                keyId = it.keyId,
                termin = it.termin,
                uriStr = it.uriStr,
            )
        }
    }

    /**
     * Image Data Diri Inden Booking
     */
    fun mapImageDataDiriIndenBooking(model: ImageDataDiriIndenBookingModel?): ImageDataDiriIndenBooking? {
        return model?.let {
            ImageDataDiriIndenBooking(
                keyId = model.keyId,
                uriStrDataDiri = model.uriStr,
            )
        }
    }

    /**
     * Pengembalian Pembayaran
     */
    fun mapPengembalian(model: PengembalianModel): Pengembalian {
        return model.let {
            Pengembalian(
                keyId = it.keyId,
                kavling = it.kavling,
                namaCustomer = it.namaCustomer,
                tanggal = it.tanggal.toDate(),
                jumlah = it.jumlah,
                keterangan = it.keterangan,
                uri = it.uri,
                timeMillis = it.timeMillis,
            )
        }
    }

    fun mapPengembalian(pengembalian: Pengembalian): PengembalianModel {
        return pengembalian.let {
            PengembalianModel(
                keyId = it.keyId,
                kavling = it.kavling,
                namaCustomer = it.namaCustomer,
                tanggal = it.tanggal.toSlashedString(),
                jumlah = it.jumlah,
                keterangan = it.keterangan,
                uri = it.uri,
                timeMillis = it.timeMillis,
            )
        }
    }

    /**
     * Tambahan Pembayaran
     */
    fun mapTambahanPembayaran(entity: TambahanPembayaran): TambahanPembayaranModel {
        return entity.let {
            TambahanPembayaranModel(
                id = it.id,
                kavling = it.kavling,
                kategori = it.kategori.kode,
                tanggal = it.tanggal.toSlashedString(),
                jumlahUang = it.jumlahUang,
                keterangan = it.keterangan,
                timeMillis = it.timeMillis,
            )
        }
    }

    // TODO: Needs additional attention for sudah isi foto
    fun mapTambahanPembayaran(model: TambahanPembayaranModel): TambahanPembayaran {
        return model.let {
            TambahanPembayaran(
                id = it.id,
                kavling = it.kavling,
                kategori = TambahanPembayaran.getKategoriFromKode(it.kategori),
                tanggal = it.tanggal.toDate(),
                jumlahUang = it.jumlahUang,
                sudahIsiFoto = false,
                keterangan = it.keterangan,
                timeMillis = it.timeMillis,
            )
        }
    }
}