package net.bagusekasaputra.griyakampoeng.tkw.data.local.model

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranIndenBookingModel

@Entity(tableName = "foto_pembayaran_inden_booking")
data class FotoPembayaranIndenBookingEntity(
    @PrimaryKey(autoGenerate = false)
    val keyId: String,
    val termin: String,
    val uriStr: String,
)

@Dao
interface FotoPembayaranIndenBookingDao {

    @Query("SELECT * FROM foto_pembayaran_inden_booking WHERE keyId=:keyId AND termin=:termin")
    fun get(keyId: String, termin: String): FotoPembayaranIndenBookingEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: FotoPembayaranIndenBookingEntity): Long

    @Query("DELETE FROM foto_pembayaran_inden_booking WHERE keyId=:keyId AND termin=:termin")
    fun delete(keyId: String, termin: String)

    @Query("DELETE FROM foto_pembayaran_inden_booking")
    fun deleteAll()
}

// Mapper
fun FotoPembayaranIndenBookingEntity.toFotoPembayaranIndenBookingModel(): FotoPembayaranIndenBookingModel {
    return FotoPembayaranIndenBookingModel(
        keyId = this.keyId,
        termin = this.termin,
        uriStr = this.uriStr
    )
}

fun FotoPembayaranIndenBookingModel.toFotoPembayaranIndenBookingEntity(): FotoPembayaranIndenBookingEntity {
    return FotoPembayaranIndenBookingEntity(
        keyId = this.keyId,
        termin = this.termin,
        uriStr = this.uriStr
    )
}