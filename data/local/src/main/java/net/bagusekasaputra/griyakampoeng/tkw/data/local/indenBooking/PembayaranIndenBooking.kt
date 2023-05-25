package net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel


@Entity(tableName = "pembayaran_inden_booking")
data class PembayaranIndenBookingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val keyId: String,
    val termin: String,
    val urutan: Int,
    val tanggal: String,
    val jumlahUangDibayar: Long,
    val keterangan: String,
    val timeMillis: Long,
)

@Dao
interface PembayaranIndenBookingDao {

    @Query("SELECT * FROM pembayaran_inden_booking WHERE keyId=:keyId")
    fun getAllByKeyId(keyId: String): List<PembayaranIndenBookingEntity>?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: PembayaranIndenBookingEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAll(entities: List<PembayaranIndenBookingEntity>): List<Long>

    @Query("DELETE FROM pembayaran_inden_booking WHERE keyId=:keyId")
    fun deleteAllWith(keyId: String)

    @Query("DELETE FROM pembayaran_inden_booking")
    fun deleteAll()
}

// Mapper
fun PembayaranIndenBookingEntity.toModel(): PembayaranModel {
    return PembayaranModel(
        termin = this.termin,
        urutan = this.urutan,
        tanggal = this.tanggal,
        jumlahUangDibayar = this.jumlahUangDibayar,
        keterangan = this.keterangan,
        timeMillis = this.timeMillis,
    )
}

fun PembayaranModel.toEntity(keyId: String): PembayaranIndenBookingEntity {
    return PembayaranIndenBookingEntity(
        keyId = keyId,
        termin = this.termin,
        urutan = this.urutan,
        tanggal = this.tanggal,
        jumlahUangDibayar = this.jumlahUangDibayar,
        keterangan = this.keterangan,
        timeMillis = this.timeMillis,
    )
}