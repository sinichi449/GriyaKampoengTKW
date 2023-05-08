package net.bagusekasaputra.griyakampoeng.tkw.data.local.baselinePembayaran

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "baselinePembayaran")
data class BaselinePembayaranRoomEntity(
    @PrimaryKey
    var kavling: String,
    var opsiBulan: Int,
    var jumlahUang: Long,
    var tanggalPembayaranMaks: Int,
    var timeMillis: Long,
)


@Dao
interface BaselinePembayaranRoomDao {

    @Query("SELECT * FROM baselinePembayaran WHERE kavling=:kavling")
    fun get(kavling: String): BaselinePembayaranRoomEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: BaselinePembayaranRoomEntity): Long

    @Query("DELETE FROM baselinePembayaran")
    fun deleteAll()
}