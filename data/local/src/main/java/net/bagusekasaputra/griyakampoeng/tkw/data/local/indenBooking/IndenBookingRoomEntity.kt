package net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "indenBooking")
data class IndenBookingRoomEntity(
    @PrimaryKey
    var timeMillis: Long = 0L,
    var namaCostumer: String,
    var tanggalDibayar: String,
    var jumlahUang: Long,
    var fotoPembayaran: String = "",
    var noHp: String = "",
    var keterangan: String = "",
)


@Dao
interface IndenBookingRoomDao {

    @Query("SELECT * FROM indenBooking")
    fun getAll(): List<IndenBookingRoomEntity>?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: IndenBookingRoomEntity): Long

    @Query("DELETE FROM indenBooking")
    fun deleteAll()

    @Query("DELETE FROM indenBooking WHERE timeMillis=:timeMillis")
    fun deleteSingle(timeMillis: Long)
}