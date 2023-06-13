package net.bagusekasaputra.griyakampoeng.tkw.data.local.model

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(
    tableName = "baselinePembayaran",
    indices = [Index(value = ["kavling"], unique = true)]
)
data class BaselinePembayaranRoomEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val kavling: String,
    var opsiBulan: Int,
    var jumlahUang: Long,
    var tanggalPembayaranMaks: Int,
    var timeMillis: Long,
)


@Dao
interface BaselinePembayaranRoomFixDao {
    @Query("SELECT * FROM baselinePembayaran WHERE kavling=:kavling")
    fun get(kavling: String): BaselinePembayaranRoomEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: BaselinePembayaranRoomEntity)

    @Insert
    fun insertAll(entities: List<BaselinePembayaranRoomEntity>): List<Long>

    @Query("DELETE FROM baselinePembayaran")
    fun deleteAll()

}