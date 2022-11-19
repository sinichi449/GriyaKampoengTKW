package net.bagusekasaputra.griyakampoengtkw.data.source.local.fotoPembayaran.room

import androidx.room.*

@Entity(tableName = "foto_pembayaran")
data class FotoPembayaranEntity(
    @PrimaryKey
    val id: Long? = null,
    @ColumnInfo(name = "kavling_kode")
    val kavlingKode: String,
    @ColumnInfo(name = "termin")
    var termin: String,
    @ColumnInfo(name = "uri")
    var uriStr: String,
)

@Dao
interface FotoPembayaranDao {

    @Query("SELECT * FROM foto_pembayaran WHERE kavling_kode=:kavlingKode AND termin=:termin")
    fun getFotoPembayaran(kavlingKode: String, termin: String): FotoPembayaranEntity?

    @Insert
    fun insert(fotoPembayaranEntity: FotoPembayaranEntity): Long

    @Query("DELETE FROM foto_pembayaran WHERE id=:id")
    fun deleteById(id: Long)

    @Query("DELETE FROM foto_pembayaran WHERE kavling_kode=:kavlingKode")
    fun deleteAllInKavling(kavlingKode: String)

    @Query("DELETE FROM foto_pembayaran WHERE kavling_kode=:kavlingKode AND termin=:termin")
    fun deleteByKavlingKodeAndTermin(kavlingKode: String, termin: String)

    @Update
    fun update(oldFotoPembayaranEntity: FotoPembayaranEntity, newPembayaranEntity: FotoPembayaranEntity)
}