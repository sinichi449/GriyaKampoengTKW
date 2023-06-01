package net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking.imageDataDiri

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import net.bagusekasaputra.griyakampoengtkw.data.model.ImageDataDiriIndenBookingModel

@Entity(
    tableName = "foto_identitas_inden_booking",
    indices = [Index(value = ["keyId", "uriStr"], unique = true)]
)
data class FotoIdentitasIndenBookingEntity(
    @PrimaryKey
    val keyId: String,
    val uriStr: String,
)

@Dao
interface FotoIdentitasIndenBookingDao {

    @Query("SELECT * FROM foto_identitas_inden_booking WHERE keyId=:keyId")
    fun getByKeyId(keyId: String): FotoIdentitasIndenBookingEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: FotoIdentitasIndenBookingEntity): Long

    @Query("DELETE FROM foto_identitas_inden_booking WHERE keyId=:keyId")
    fun delete(keyId: String)

    @Query("DELETE FROM foto_identitas_inden_booking")
    fun deleteAll()

}

// Mapper
fun FotoIdentitasIndenBookingEntity.toModel(): ImageDataDiriIndenBookingModel {
    return ImageDataDiriIndenBookingModel(
        keyId = this.keyId,
        uriStr = this.uriStr,
    )
}

fun ImageDataDiriIndenBookingModel.toEntity(): FotoIdentitasIndenBookingEntity {
    return FotoIdentitasIndenBookingEntity(
        keyId = this.keyId,
        uriStr = this.uriStr,
    )
}
