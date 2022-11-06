package net.bagusekasaputra.griyakampoengtkw.data.source.local.imageDataDiri.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_data_diri")
data class ImageDataDiriRoom(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "kavling_kode") val kavlingKode: String,
    @ColumnInfo(name = "uri") val imgUri: String,
)