package net.bagusekasaputra.griyakampoengtkw.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import net.bagusekasaputra.griyakampoengtkw.data.source.local.block.room.BlockRoomDao
import net.bagusekasaputra.griyakampoengtkw.data.source.local.block.room.BlockRoomEntity
import net.bagusekasaputra.griyakampoengtkw.data.source.local.datadiri.room.DataDiriRoomDao
import net.bagusekasaputra.griyakampoengtkw.data.source.local.datadiri.room.DataDiriRoomEntity
import net.bagusekasaputra.griyakampoengtkw.data.source.local.fotoKuitansi.room.FotoKuitansiRoomDao
import net.bagusekasaputra.griyakampoengtkw.data.source.local.fotoKuitansi.room.FotoKuitansiRoomEntity
import net.bagusekasaputra.griyakampoengtkw.data.source.local.imageDataDiri.room.ImageDataDiriDao
import net.bagusekasaputra.griyakampoengtkw.data.source.local.imageDataDiri.room.ImageDataDiriRoomEntity
import net.bagusekasaputra.griyakampoengtkw.data.source.local.kavling.room.KavlingRoomDao
import net.bagusekasaputra.griyakampoengtkw.data.source.local.kavling.room.KavlingRoomEntity

@Database(
    entities = [KavlingRoomEntity::class, ImageDataDiriRoomEntity::class, BlockRoomEntity::class,
               DataDiriRoomEntity::class, FotoKuitansiRoomEntity::class],
    version = 2,
    exportSchema = false
)
abstract class MyRoomDatabase: RoomDatabase() {

    abstract fun getKavlingDao(): KavlingRoomDao

    abstract fun getImageDataDiriDao(): ImageDataDiriDao

    abstract fun getBlockDao(): BlockRoomDao

    abstract fun getDataDiriDao(): DataDiriRoomDao

    abstract fun getFotoKuitansiDao(): FotoKuitansiRoomDao
}