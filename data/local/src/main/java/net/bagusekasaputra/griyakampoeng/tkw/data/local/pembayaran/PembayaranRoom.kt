package net.bagusekasaputra.griyakampoeng.tkw.data.local.pembayaran

import androidx.room.*
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranModel

@Entity(
    tableName = "pembayaran",
)
data class PembayaranRoomEntity(
    @PrimaryKey
    var id: Long? = null,
    @ColumnInfo(name = "kavling_kode")
    val kavlingKode: String,
    // Termin in the Room database will represents the Full Termin instead of Parted Termin.
    // Full Termin -> "DP 3"
    // Parted Termin -> Termin: "DP", Urutan: "3"
    @ColumnInfo(name = "termin")
    val termin: String,
    @ColumnInfo(name = "tanggal")
    var tanggal: String,
    @ColumnInfo(name = "jumlah_uang_dibayar")
    var jumlahUangDibayar: Long,
    @ColumnInfo(name = "invoice_date")
    var invoiceDate: String,
    @ColumnInfo(name = "keterangan")
    var keterangan: String,
    @ColumnInfo(name = "timeMillis")
    var timeMillis: Long,
)

@Dao
interface PembayaranRoomDao {

    @Query("SELECT * FROM pembayaran WHERE kavling_kode=:kavlingKode")
    fun getAllPembayaran(kavlingKode: String): List<PembayaranRoomEntity>?

    @Query("SELECT * FROM pembayaran WHERE kavling_kode=:kavlingKode AND termin=:termin")
    fun getSinglePembayaran(kavlingKode: String, termin: String): PembayaranRoomEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPembayaran(entity: PembayaranRoomEntity): Long

    @Query("UPDATE pembayaran SET " +
            "termin=:newTermin, tanggal=:tanggal, jumlah_uang_dibayar=:jumlahUangDibayar, " +
            "keterangan=:keterangan, timeMillis=:timeMillis, invoice_date=:invoiceDate " +
            "WHERE kavling_kode=:kavlingKode AND termin=:termin")
    fun updatePembayaran(
        kavlingKode: String,
        termin: String,
        newTermin: String,
        tanggal: String,
        jumlahUangDibayar: Long,
        invoiceDate: String,
        keterangan: String,
        timeMillis: Long,
    )

    @Query("DELETE FROM pembayaran WHERE kavling_kode=:kavlingKode AND termin=:termin")
    fun deleteByKavlingKodeAndTermin(kavlingKode: String, termin: String)

    @Query("DELETE FROM pembayaran WHERE kavling_kode=:kavlingKode")
    fun deleteAllInKavling(kavlingKode: String)

    @Query("DELETE FROM pembayaran")
    fun deleteAll()

}

/**
 * Mapper
 */
fun PembayaranRoomEntity.toModel(): PembayaranModel {
    val separateTerminAndUrutan = PembayaranModel.pisahkanTerminDanUrutan(this.termin)

    return PembayaranModel(
        termin = separateTerminAndUrutan[PembayaranModel.KEY_JENIS_TERMIN]!!,
        urutan = separateTerminAndUrutan[PembayaranModel.KEY_URUTAN_TERMIN]!!.toInt(),
        jumlahUangDibayar = this.jumlahUangDibayar,
        tanggal = this.tanggal,
        keterangan = this.keterangan,
        timeMillis = this.timeMillis,
        invoiceDateStr = this.invoiceDate,
    )
}

fun PembayaranModel.toEntity(kavlingKode: String): PembayaranRoomEntity {
    return PembayaranRoomEntity(
        kavlingKode = kavlingKode,
        termin = getFullTermin(),
        tanggal = tanggal,
        jumlahUangDibayar = jumlahUangDibayar,
        invoiceDate = invoiceDateStr,
        keterangan = keterangan,
        timeMillis = timeMillis,
    )
}