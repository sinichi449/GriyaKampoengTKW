package net.bagusekasaputra.griyakampoeng.tkw.data.local.rekap

import androidx.room.*

@Entity(tableName = "rekap_uang_masuk")
data class RekapUangMasukEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    @ColumnInfo(name = "no_kavling")
    val noKavling: String,
    @ColumnInfo(name = "nama_costumer")
    val namaCostumer: String,
    val tanggal: String,
    @ColumnInfo(name = "jenis_pembayaran")
    val jenisPembayaran: String,
    @ColumnInfo(name = "jumlah_pembayaran")
    val jumlahPembayaran: Long,
)

@Dao
interface RekapUangMasukDao {

    @Query("SELECT * FROM rekap_uang_masuk")
    fun getAll(): List<RekapUangMasukEntity>?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(entity: RekapUangMasukEntity)

    @Query("DELETE FROM rekap_uang_masuk")
    fun clearAll()
}