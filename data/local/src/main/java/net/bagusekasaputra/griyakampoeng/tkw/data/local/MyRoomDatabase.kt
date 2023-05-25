package net.bagusekasaputra.griyakampoeng.tkw.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.baselinePembayaran.BaselinePembayaranRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.baselinePembayaran.BaselinePembayaranRoomFixDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.biayaLain.BiayaLainDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.biayaLain.BiayaLainRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.biayaMarketing.BiayaMarketingV2RoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.biayaMarketing.BiayaMarketingV2RoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.block.BlockRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.block.BlockRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.catatanPembayaran.CatatanPembayaranRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.catatanPembayaran.CatatanPembayaranRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.datadiri.DataDiriRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.datadiri.DataDiriRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.feeMarketing.FeeMarketingRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.feeMarketing.FeeMarketingRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.fotoKuitansi.FotoKuitansiRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.fotoKuitansi.FotoKuitansiRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.fotoPembayaran.FotoPembayaranDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.fotoPembayaran.FotoPembayaranEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.hargaKavling.HargaKavlingRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.hargaKavling.HargaKavlingRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.imageDataDiri.ImageDataDiriDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.imageDataDiri.ImageDataDiriRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.imageSpr.ImageSprRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.imageSpr.ImageSprRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking.FotoIdentitasIndenBookingDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking.FotoIdentitasIndenBookingEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.kavling.KavlingRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.kavling.KavlingRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.metadata.MetadataDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.metadata.MetadataEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.pembayaran.PembayaranRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.pembayaran.PembayaranRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.pengingat.PengingatRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.pengingat.PengingatRoomEntity

@Database(
    entities = [KavlingRoomEntity::class, ImageDataDiriRoomEntity::class, BlockRoomEntity::class,
               DataDiriRoomEntity::class, FotoKuitansiRoomEntity::class, ImageSprRoomEntity::class,
               FotoPembayaranEntity::class, PembayaranRoomEntity::class, HargaKavlingRoomEntity::class,
               CatatanPembayaranRoomEntity::class, FeeMarketingRoomEntity::class,
                BiayaMarketingV2RoomEntity::class, PengingatRoomEntity::class, MetadataEntity::class,
               BiayaLainRoomEntity::class, BaselinePembayaranRoomEntity::class,
               FotoIdentitasIndenBookingEntity::class],
    version = 23,
    exportSchema = true,
)
abstract class MyRoomDatabase: RoomDatabase() {

    abstract fun getMetadataDao(): MetadataDao

    abstract fun getKavlingDao(): KavlingRoomDao

    abstract fun getImageDataDiriDao(): ImageDataDiriDao

    abstract fun getBlockDao(): BlockRoomDao

    abstract fun getDataDiriDao(): DataDiriRoomDao

    abstract fun getFotoKuitansiDao(): FotoKuitansiRoomDao

    abstract fun getImageSprDao(): ImageSprRoomDao

    abstract fun getFotoPembayaranDao(): FotoPembayaranDao

    abstract fun getPembayaranDao(): PembayaranRoomDao

    abstract fun getHargaKavlingDao(): HargaKavlingRoomDao

    abstract fun getCatatanPembayaranDao(): CatatanPembayaranRoomDao

    abstract fun getBiayaMarketingV2Dao(): BiayaMarketingV2RoomDao

    abstract fun getFeeMarketingDao(): FeeMarketingRoomDao

    abstract fun getPengingatDao(): PengingatRoomDao

    abstract fun getBiayaLainDao(): BiayaLainDao

    abstract fun getBaselinePembayaranDao(): BaselinePembayaranRoomFixDao

    abstract fun getFotoIdentitasIndenBookingDao(): FotoIdentitasIndenBookingDao
}