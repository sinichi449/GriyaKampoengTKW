package net.bagusekasaputra.griyakampoeng.tkw.data.local

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.block.BlockRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.block.BlockRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.datadiri.DataDiriRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.datadiri.DataDiriRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.fotoKuitansi.FotoKuitansiRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.fotoKuitansi.FotoKuitansiRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.fotoPembayaran.FotoPembayaranDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.fotoPembayaran.FotoPembayaranEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.imageDataDiri.ImageDataDiriDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.imageDataDiri.ImageDataDiriRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.imageSpr.ImageSprRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.imageSpr.ImageSprRoomEntity
import net.bagusekasaputra.griyakampoeng.tkw.data.local.kavling.KavlingRoomDao
import net.bagusekasaputra.griyakampoeng.tkw.data.local.kavling.KavlingRoomEntity

@Database(
    entities = [KavlingRoomEntity::class, ImageDataDiriRoomEntity::class, BlockRoomEntity::class,
               DataDiriRoomEntity::class, FotoKuitansiRoomEntity::class, ImageSprRoomEntity::class,
               FotoPembayaranEntity::class],
    version = 4,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 3, to = 4)
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
}