package net.bagusekasaputra.griyakampoeng.tkw.data.local.biayaMarketing

import androidx.room.*

@Entity(tableName = "biaya_marketing_v2")
data class BiayaMarketingV2RoomEntity(
    @PrimaryKey
    var id: Long? = null,
    @ColumnInfo(name = "kavling_kode")
    val kavlingKode: String = "",
    @ColumnInfo(name = "tanggal")
    var tanggal: String = "",
    @ColumnInfo(name = "jenis_biaya")
    val jenisBiaya: String = "",
    @ColumnInfo(name = "harga")
    val harga: Long = 0L,
)



@Dao
interface BiayaMarketingV2RoomDao {

    @Query("SELECT * FROM biaya_marketing_v2 WHERE kavling_kode=:kavlingKode")
    fun getAll(kavlingKode: String): List<BiayaMarketingV2RoomEntity>?

    @Query("SELECT * FROM biaya_marketing_v2 WHERE tanggal=:tanggal " +
            "AND kavling_kode=:kavlingKode " +
            "AND jenis_biaya=:jenisBiaya " +
            "AND harga=:harga")
    fun getBiayaMarketing(tanggal: String, kavlingKode: String, jenisBiaya: String, harga: Long): BiayaMarketingV2RoomEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBiayaMarketing(entity: BiayaMarketingV2RoomEntity): Long


    @Query("UPDATE biaya_marketing_v2 SET " +
            "tanggal=:newTanggal, kavling_kode=:kavlingKode, jenis_biaya=:newJenisBiaya, harga=:newHarga " +
            "WHERE id=:id")
    fun updateBiayaMarketing(id: Long, newTanggal: String, kavlingKode: String, newJenisBiaya: String, newHarga: Long)

    @Query("DELETE FROM biaya_marketing_v2 WHERE id=:id")
    fun deleteById(id: Long)

    @Query("DELETE FROM biaya_marketing_v2 WHERE kavling_kode=:kavlingKode")
    fun deleteAll(kavlingKode: String)


}