package net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking.hargaRumah

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaRumahModel

@Entity(tableName = "harga_rumah_inden_booking")
data class HargaRumahEntity(
    @PrimaryKey(autoGenerate = false)
    val keyId: String,
    val harga: Long,
    val tambahLuasan: Long,
)


@Dao
interface HargaRumahDao {

    @Query("SELECT * FROM harga_rumah_inden_booking WHERE keyId=:keyId")
    fun get(keyId: String): HargaRumahEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: HargaRumahEntity): Long

    @Query("DELETE FROM harga_rumah_inden_booking WHERE keyId=:keyId")
    fun delete(keyId: String)

    @Query("DELETE FROM harga_rumah_inden_booking")
    fun deleteAll()

}

// Mapper
fun HargaRumahModel.toEntity(): HargaRumahEntity {
    return this.let {
        HargaRumahEntity(
            keyId = it.keyId,
            harga = it.harga,
            tambahLuasan = it.tambahLuasan,
        )
    }
}

fun HargaRumahEntity.toModel(): HargaRumahModel {
    return this.let {
        HargaRumahModel(
            keyId = it.keyId,
            harga = it.harga,
            tambahLuasan = it.tambahLuasan,
        )
    }
}