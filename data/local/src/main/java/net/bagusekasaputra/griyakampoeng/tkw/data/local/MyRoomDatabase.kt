package net.bagusekasaputra.griyakampoeng.tkw.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.biayaMarketing.BiayaMarketingRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.biayaMarketing.BiayaMarketingRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.block.BlockRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.block.BlockRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.catatanPembayaran.CatatanPembayaranRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.catatanPembayaran.CatatanPembayaranRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.datadiri.DataDiriRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.datadiri.DataDiriRoomEntity
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
import net.bagusekasaputra.griyakampoeng.tkw.data.local.kavling.KavlingRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.kavling.KavlingRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.pembayaran.PembayaranRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.pembayaran.PembayaranRoomEntity

@Database(
    entities = [KavlingRoomEntity::class, ImageDataDiriRoomEntity::class, BlockRoomEntity::class,
               DataDiriRoomEntity::class, FotoKuitansiRoomEntity::class, ImageSprRoomEntity::class,
               FotoPembayaranEntity::class, PembayaranRoomEntity::class, HargaKavlingRoomEntity::class,
               CatatanPembayaranRoomEntity::class, BiayaMarketingRoomEntity::class],
    version = 8,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8),
    ]
)
abstract class MyRoomDatabase: RoomDatabase() {

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

    abstract fun getBiayaMarketingDao(): BiayaMarketingRoomDao
}