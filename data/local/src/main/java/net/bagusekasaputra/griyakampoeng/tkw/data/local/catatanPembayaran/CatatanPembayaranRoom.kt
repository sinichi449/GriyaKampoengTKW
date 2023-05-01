package net.bagusekasaputra.griyakampoeng.tkw.data.local.catatanPembayaran

import androidx.room.*

@Entity(
    tableName = "catatan_pembayaran",
    indices = [
        Index(value = ["kavling_kode"], unique = true)
    ]
)
data class CatatanPembayaranRoomEntity(
    @PrimaryKey
    var id: Long? = null,
    @ColumnInfo(name = "kavling_kode")
    val kavlingKode: String,
    val content: String,
)


@Dao
interface CatatanPembayaranRoomDao {

    @Query("SELECT * FROM catatan_pembayaran WHERE kavling_kode=:kavlingKode")
    fun getCatatan(kavlingKode: String): CatatanPembayaranRoomEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addCatatan(catatanPembayaranRoomEntity: CatatanPembayaranRoomEntity): Long

    @Query("UPDATE catatan_pembayaran SET " +
            "content=:newContent " +
            "WHERE kavling_kode=:kavlingKode")
    fun updateCatatan(kavlingKode: String, newContent: String)

    @Query("DELETE FROM catatan_pembayaran WHERE kavling_kode=:kavlingKode")
    fun deleteCatatan(kavlingKode: String)
}