package net.bagusekasaputra.griyakampoeng.tkw.data.local.imageSpr

import androidx.room.*

@Entity(
    tableName = "image_spr",
    indices = [
        Index(value = ["kavling_kode"], unique = true),
    ]
)
data class ImageSprRoomEntity(
    @PrimaryKey
    var id: Long? = null,
    @ColumnInfo(name = "kavling_kode")
    val kavlingKode: String,
    @ColumnInfo(name = "uri")
    var imgUri: String,
)


@Dao
interface ImageSprRoomDao {

    @Query("SELECT * FROM image_spr WHERE kavling_kode=:kavlingKode")
    fun getByKavlingKode(kavlingKode: String): ImageSprRoomEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(imageSprRoomEntity: ImageSprRoomEntity)

    @Query("DELETE FROM image_spr")
    fun deleteAll()

}