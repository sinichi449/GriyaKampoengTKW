package net.bagusekasaputra.griyakampoeng.tkw.data.local.biayaMarketing

import androidx.room.*

@Entity(
    tableName = "biaya_marketing",
    indices = [
        Index(value = ["timeMillis"], unique = true)
    ]
)
data class BiayaMarketingRoomEntity(
    @PrimaryKey
    var id: Long? = null,
    val timeMillis: Long = 0L,
    @ColumnInfo(name = "kavling_kode")
    val kavlingKode: String = "",
    @ColumnInfo(name = "jenis_biaya")
    val jenisBiaya: String = "",
    @ColumnInfo(name = "harga")
    val harga: Long = 0L,
)



@Dao
interface BiayaMarketingRoomDao {

    @Query("SELECT * FROM biaya_marketing WHERE kavling_kode=:kavlingKode")
    fun getAll(kavlingKode: String): List<BiayaMarketingRoomEntity>?

    @Query("SELECT * FROM biaya_marketing WHERE kavling_kode=:kavlingKode AND jenis_biaya=:jenisBiaya AND harga=:harga")
    fun getBiayaMarketing(kavlingKode: String, jenisBiaya: String, harga: Long): BiayaMarketingRoomEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBiayaMarketing(entity: BiayaMarketingRoomEntity): Long


    @Query("UPDATE biaya_marketing SET " +
            "jenis_biaya=:newJenisBiaya, harga=:newHarga " +
            "WHERE kavling_kode=:kavlingKode")
    fun updateBiayaMarketing(kavlingKode: String, newJenisBiaya: String, newHarga: Long)


    @Query("DELETE FROM biaya_marketing WHERE kavling_kode=:kavlingKode AND timeMillis=:timeMillis")
    fun deleteSingle(kavlingKode: String, timeMillis: Long)


    @Query("DELETE FROM biaya_marketing WHERE kavling_kode=:kavlingKode")
    fun deleteAll(kavlingKode: String)
}