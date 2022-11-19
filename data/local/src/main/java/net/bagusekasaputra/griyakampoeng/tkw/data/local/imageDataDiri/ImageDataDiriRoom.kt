package net.bagusekasaputra.griyakampoeng.tkw.data.local.imageDataDiri

import androidx.room.*

@Entity(tableName = "image_data_diri")
data class ImageDataDiriRoomEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "kavling_kode") val kavlingKode: String,
    @ColumnInfo(name = "uri") val imgUri: String,
)


@Dao
interface ImageDataDiriDao {

    @Query("SELECT * FROM image_data_diri WHERE kavling_kode = :kavlingKode")
    fun getByKavlingKode(kavlingKode: String): ImageDataDiriRoomEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(imageDataDiri: ImageDataDiriRoomEntity)

    @Delete
    fun delete(imageDataDiri: ImageDataDiriRoomEntity)

    @Query("DELETE FROM image_data_diri WHERE kavling_kode = :kavlingKode")
    fun deleteByKavlingKode(kavlingKode: String)

    @Update
    fun update(oldImageDataDiri: ImageDataDiriRoomEntity, newImageDataDiri: ImageDataDiriRoomEntity)
}