package net.bagusekasaputra.griyakampoeng.tkw.data.local.model

import androidx.room.*

@Entity(
    tableName = "kavlings",
    indices = [
        Index(value = ["kode"], unique = true)
    ]
)
data class KavlingRoomEntity(
    @PrimaryKey
    var id: Long? = null,
    @ColumnInfo(name = "block_kode")
    val blockKode: String,
    @ColumnInfo(name = "kode")
    val kode: String,
    @ColumnInfo(name = "warna")
    var warna: String,
    @ColumnInfo(name = "is_active")
    var isActive: Boolean,
    @ColumnInfo(name = "ukuran")
    var ukuran: String,
    @ColumnInfo(name = "type")
    var type: String,
)

@Dao
interface KavlingRoomDao {

    @Query("SELECT * FROM kavlings WHERE block_kode=:blockKode")
    fun getKavlingsByBlockKode(blockKode: String): List<KavlingRoomEntity>?

    @Query("SELECT * FROM kavlings WHERE block_kode=:blockKode AND kode=:kode")
    fun getSingleKavling(blockKode: String, kode: String): KavlingRoomEntity?

    @Query("UPDATE kavlings SET warna=:warna, is_active=:isActive, ukuran=:ukuran, type=:type WHERE kode=:kode")
    fun updateKavling(kode: String, warna: String, isActive: Boolean, ukuran: String, type: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(kavlingRoom: KavlingRoomEntity): Long

    @Query("DELETE FROM kavlings WHERE kode=:kavlingKode")
    fun deleteKavling(kavlingKode: String)

    @Query("DELETE FROM kavlings")
    fun deleteAll()
}