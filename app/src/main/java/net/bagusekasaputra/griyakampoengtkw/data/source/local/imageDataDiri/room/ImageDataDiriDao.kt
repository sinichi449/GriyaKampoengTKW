package net.bagusekasaputra.griyakampoengtkw.data.source.local.imageDataDiri.room

import androidx.room.*

@Dao
interface ImageDataDiriDao {

    @Query("SELECT * FROM image_data_diri WHERE kavling_kode = :kavlingKode")
    fun getByKavlingKode(kavlingKode: String): ImageDataDiriRoom

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(imageDataDiri: ImageDataDiriRoom)

    @Delete
    fun delete(imageDataDiri: ImageDataDiriRoom)

    @Query("DELETE FROM image_data_diri WHERE kavling_kode = :kavlingKode")
    fun deleteByKavlingKode(kavlingKode: String)

    @Update
    fun update(oldImageDataDiri: ImageDataDiriRoom, newImageDataDiri: ImageDataDiriRoom)
}