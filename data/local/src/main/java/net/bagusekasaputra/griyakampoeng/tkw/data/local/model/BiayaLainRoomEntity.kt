package net.bagusekasaputra.griyakampoeng.tkw.data.local.model

import androidx.room.*

@Entity(
    tableName = "biaya_lain",
    indices = [
        Index(value = ["jenis_biaya"], unique = true)
    ],
)
data class BiayaLainRoomEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    @ColumnInfo(name = "jenis_biaya")
    val jenisBiaya: String,
    val harga: Long,
    val tanggal: String,
)


@Dao
interface BiayaLainDao {

    @Query("SELECT * FROM biaya_lain")
    fun getAll(): List<BiayaLainRoomEntity>?

    @Query("SELECT * FROM biaya_lain WHERE jenis_biaya=:jenisBiaya")
    fun getSingle(jenisBiaya: String): BiayaLainRoomEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: BiayaLainRoomEntity): Long

    @Query("UPDATE biaya_lain SET " +
            "jenis_biaya=:newJenisBiaya, harga=:newHarga, tanggal=:newTanggal " +
            "WHERE jenis_biaya=:jenisBiaya")
    fun update(
        jenisBiaya: String,
        newJenisBiaya: String,
        newHarga: Long,
        newTanggal: String,
    )

    @Query("DELETE FROM biaya_lain WHERE jenis_biaya=:jenisBiaya")
    fun delete(jenisBiaya: String)

    @Query("DELETE FROM biaya_lain")
    fun deleteAll()

}

