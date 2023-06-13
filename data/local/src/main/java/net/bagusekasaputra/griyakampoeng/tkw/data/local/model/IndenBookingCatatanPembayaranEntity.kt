package net.bagusekasaputra.griyakampoeng.tkw.data.local.model

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import net.bagusekasaputra.griyakampoengtkw.data.model.IndenBookingCatatanPembayaranModel

@Entity(
    tableName = "catatan_pembayaran_inden_booking",
    indices = [Index(value = ["keyId"], unique = true)]
)
data class IndenBookingCatatanPembayaranEntity(
    @PrimaryKey(autoGenerate = false)
    val keyId: String,
    val content: String,
)

@Dao
interface IndenBookingCatatanPembayaranDao {

    @Query("SELECT * FROM catatan_pembayaran_inden_booking WHERE keyId=:keyId")
    fun get(keyId: String): IndenBookingCatatanPembayaranEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: IndenBookingCatatanPembayaranEntity): Long

    @Query("DELETE FROM catatan_pembayaran_inden_booking WHERE keyId=:keyId")
    fun delete(keyId: String)

    @Query("DELETE FROM catatan_pembayaran_inden_booking")
    fun deleteAll()

}

/**
 * Mapper
 */
fun IndenBookingCatatanPembayaranEntity.toIndenBookingCatatanPembayaranEntity(): IndenBookingCatatanPembayaranModel {
    return IndenBookingCatatanPembayaranModel(
        keyId = this.keyId,
        content = this.content,
    )
}

fun IndenBookingCatatanPembayaranModel.toIndenBookingCatatanPembayaranModel(): IndenBookingCatatanPembayaranEntity {
    return IndenBookingCatatanPembayaranEntity(
        keyId = this.keyId,
        content = this.content,
    )
}