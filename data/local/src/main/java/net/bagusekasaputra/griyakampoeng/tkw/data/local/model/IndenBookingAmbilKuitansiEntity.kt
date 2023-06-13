package net.bagusekasaputra.griyakampoeng.tkw.data.local.model

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import net.bagusekasaputra.griyakampoengtkw.data.model.IndenBookingAmbilKuitansiModel

@Entity(
    tableName = "ambil_kuitansi_inden_booking",
    indices = [Index(value = ["keyId"], unique = false)]
)
data class IndenBookingAmbilKuitansiEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    val keyId: String,
    val termin: String,
    val sudahAmbil: Boolean,
)

@Dao
interface IndenBookingAmbilKuitansiDao {

    @Query("SELECT * FROM ambil_kuitansi_inden_booking WHERE keyId=:keyId AND termin=:termin")
    fun get(keyId: String, termin: String): IndenBookingAmbilKuitansiEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: IndenBookingAmbilKuitansiEntity): Long

    @Query("DELETE FROM ambil_kuitansi_inden_booking WHERE keyId=:keyId AND termin=:termin")
    fun delete(keyId: String, termin: String)

    @Query("DELETE FROM ambil_kuitansi_inden_booking")
    fun deleteAll()

}

/**
 * Mapper
 */
fun IndenBookingAmbilKuitansiEntity.toIndenBookingAmbilKuitansiModel(): IndenBookingAmbilKuitansiModel {
    return IndenBookingAmbilKuitansiModel(
        keyId = this.keyId,
        termin = this.termin,
        sudahAmbil = this.sudahAmbil,
    )
}

fun IndenBookingAmbilKuitansiModel.toIndenBookingAmbilKuitansiEntity(): IndenBookingAmbilKuitansiEntity {
    return IndenBookingAmbilKuitansiEntity(
        keyId = this.keyId,
        termin = this.termin,
        sudahAmbil = this.sudahAmbil,
    )
}