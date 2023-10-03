package net.bagusekasaputra.griyakampoeng.tkw.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "foto_tambahan_pembayaran")
data class FotoTambahanPembayaranEntity(
    @PrimaryKey
    val id: Long? = null,
    @ColumnInfo(name = "kavling")
    val kavling: String,
    @ColumnInfo(name = "pembayaranId")
    val pembayaranId: String,
    @ColumnInfo(name = "uri")
    val uri: String,
)

@Dao
interface FotoTambahanPembayaranDao {

    @Query("SELECT * FROM foto_tambahan_pembayaran WHERE kavling=:kavling AND pembayaranId=:pembayaranId")
    fun get(kavling: String, pembayaranId: String): FotoTambahanPembayaranEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: FotoTambahanPembayaranEntity): Long

}