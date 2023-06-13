package net.bagusekasaputra.griyakampoeng.tkw.data.local.model

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import net.bagusekasaputra.griyakampoengtkw.data.model.PengembalianModel

@Entity(
    tableName = "pengembalian_pembayaran",
    indices = [Index(value = ["kavling"], unique = false)],
)
data class PengembalianEntity(
    @PrimaryKey(autoGenerate = false)
    val keyId: String,
    val kavling: String,
    val namaCustomer: String,
    val tanggal: String,
    val jumlah: Long,
    val keterangan: String,
    val uri: String,
    val timeMillis: Long,
)

@Dao
interface PengembalianDao {

    @Query("SELECT * FROM pengembalian_pembayaran WHERE keyId=:keyId")
    fun get(keyId: String): PengembalianEntity?

    @Query("SELECT * FROM pengembalian_pembayaran")
    fun getAll(): List<PengembalianEntity>?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: PengembalianEntity): Long

    @Query("DELETE FROM pengembalian_pembayaran")
    fun deleteAll()

}

/**
 * Mapper
 */
fun PengembalianEntity.toPengembalianModel(): PengembalianModel {
    return PengembalianModel(
        keyId = keyId,
        kavling = kavling,
        namaCustomer = namaCustomer,
        tanggal = tanggal,
        jumlah = jumlah,
        keterangan = keterangan,
        uri = uri,
        timeMillis = timeMillis,
    )
}

fun PengembalianModel.toPengembalianEntity(): PengembalianEntity {
    return PengembalianEntity(
        keyId = keyId,
        kavling = kavling,
        namaCustomer = namaCustomer,
        tanggal = tanggal,
        jumlah = jumlah,
        keterangan = keterangan,
        uri = uri,
        timeMillis = timeMillis,
    )
}