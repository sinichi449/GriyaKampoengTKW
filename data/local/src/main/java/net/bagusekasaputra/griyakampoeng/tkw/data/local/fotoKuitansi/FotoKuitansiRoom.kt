package net.bagusekasaputra.griyakampoeng.tkw.data.local.fotoKuitansi

import androidx.room.*

@Entity(
    tableName = "foto_kuitansi",
    indices = [
        Index(value = ["kavling_kode"], unique = true)
    ]
)
data class FotoKuitansiRoomEntity(
    @PrimaryKey
    var id: Long? = null,
    @ColumnInfo(name = "kavling_kode")
    val kavlingKode: String,
    @ColumnInfo(name = "foto_uri")
    var fotoUri: String?,
)


@Dao
interface FotoKuitansiRoomDao {

    @Query("SELECT * FROM foto_kuitansi WHERE kavling_kode=:kavlingKode")
    fun getByKavlingKode(kavlingKode: String): FotoKuitansiRoomEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(fotoKuitansiRoomEntity: FotoKuitansiRoomEntity): Long

}



















