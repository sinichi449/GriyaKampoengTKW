package net.bagusekasaputra.griyakampoeng.tkw.data.local.model

import androidx.room.*

@Entity(
    tableName = "harga_kavling",
    indices = [
        Index(value = ["kavling_kode"], unique = true)
    ]
)
data class HargaKavlingRoomEntity(
    @PrimaryKey
    var id: Long? = null,
    @ColumnInfo(name = "kavling_kode")
    val kavlingKode: String,
    val harga: Long,
    @ColumnInfo(name = "tambah_luasan")
    val tambahLuasan: Long,
)


@Dao
interface HargaKavlingRoomDao {

    @Query("SELECT * FROM harga_kavling WHERE kavling_kode=:kavlingKode")
    fun getHargaKavling(kavlingKode: String): HargaKavlingRoomEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertHargaKavling(hargaKavlingRoomEntity: HargaKavlingRoomEntity): Long

    @Query("UPDATE harga_kavling SET " +
            "harga=:newHarga, tambah_luasan=:newTambahLuasan " +
            "WHERE kavling_kode=:kavlingKode")
    fun updateHargaKavling(kavlingKode: String, newHarga: Long, newTambahLuasan: Long)

    @Query("DELETE FROM harga_kavling WHERE kavling_kode=:kavlingKode")
    fun deleteHargaKavling(kavlingKode: String)

    @Query("DELETE FROM harga_kavling")
    fun deleteAll()
}