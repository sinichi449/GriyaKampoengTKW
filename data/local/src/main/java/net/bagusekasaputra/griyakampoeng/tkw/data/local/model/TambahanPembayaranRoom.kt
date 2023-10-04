package net.bagusekasaputra.griyakampoeng.tkw.data.local.model

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "tambahanPembayaran")
data class TambahanPembayaranEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val kavling: String,
    val pembayaranId: String,
    val kategori: String,
    val tanggal: String,
    val jumlahUang: Long,
    val keterangan: String,
    val timeMillis: Long,
)

@Dao
interface TambahanPembayaranDao {

    @Query("SELECT * FROM tambahanPembayaran WHERE kavling=:kavling")
    fun getAll(kavling: String): List<TambahanPembayaranEntity>?

    @Query("SELECT * FROM tambahanPembayaran WHERE kavling=:kavling AND pembayaranId=:pembayaranId")
    fun getById(kavling: String, pembayaranId: String): TambahanPembayaranEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: TambahanPembayaranEntity): Long

    @Query("DELETE FROM tambahanPembayaran")
    fun deleteAll()
}