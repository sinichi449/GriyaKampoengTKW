package net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import net.bagusekasaputra.griyakampoengtkw.data.model.DataDiriModel

@Entity(
    tableName = "data_diri_inden_booking",
    indices = [Index(value = ["keyId", "nama"], unique = true)]
)
data class DataDiriIndenBookingEntity(
    @PrimaryKey
    val keyId: String,
    val nama: String,
    val jenisIdentitas: String,
    val noIdentitas: String,
    val negaraBekerja: String,
    val alamatKerja: String,
    val alamatIndo: String,
    val noHp: String,
)

@Dao
interface DataDiriIndenBookingDao {

    @Query("SELECT * FROM data_diri_inden_booking WHERE keyId=:keyId")
    fun getByKeyId(keyId: String): DataDiriIndenBookingEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: DataDiriIndenBookingEntity): Long

    @Query("DELETE FROM data_diri_inden_booking WHERE keyId=:keyId")
    fun delete(keyId: String)

    @Query("DELETE FROM data_diri_inden_booking")
    fun deleteAll()

}

/**
 * Mapper
 */
fun DataDiriIndenBookingEntity.toModel(): DataDiriModel {
    return this.let {
        DataDiriModel(
            nama = it.nama,
            jenisIdentitas = it.jenisIdentitas,
            noIdentitas = it.noIdentitas,
            negaraBekerja = it.negaraBekerja,
            alamatKerja = it.alamatKerja,
            alamatIndo = it.alamatIndo,
            noHp = it.noHp,
        )
    }
}

fun DataDiriModel.toEntity(keyId: String): DataDiriIndenBookingEntity {
    return this.let {
        DataDiriIndenBookingEntity(
            nama = it.nama,
            jenisIdentitas = it.jenisIdentitas,
            noIdentitas = it.noIdentitas,
            negaraBekerja = it.negaraBekerja,
            alamatKerja = it.alamatKerja,
            alamatIndo = it.alamatIndo,
            noHp = it.noHp,
            keyId = keyId,
        )
    }
}