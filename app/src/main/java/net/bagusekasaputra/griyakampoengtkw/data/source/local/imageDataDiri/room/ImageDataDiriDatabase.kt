package net.bagusekasaputra.griyakampoengtkw.data.source.local.imageDataDiri.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ImageDataDiriRoom::class], version = 1, exportSchema = false)
abstract class ImageDataDiriDatabase: RoomDatabase() {
    abstract fun getDao(): ImageDataDiriDao
}