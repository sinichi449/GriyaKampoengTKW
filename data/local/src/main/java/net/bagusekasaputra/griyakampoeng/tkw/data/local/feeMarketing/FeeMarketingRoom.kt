package net.bagusekasaputra.griyakampoeng.tkw.data.local.feeMarketing

import androidx.room.*

@Entity(tableName = "fee_marketing")
data class FeeMarketingRoomEntity(
    @PrimaryKey
    var id: Long? = null,
    var timeMillis: Long? = null,
    @ColumnInfo(name = "kavling_kode")
    val kavlingKode: String = "",
    @ColumnInfo(name = "nama_marketer")
    val namaMarketer: String = "",
    @ColumnInfo(name = "biaya_marketer")
    val biayaMarketer: Long = 0L,
)


@Dao
interface FeeMarketingRoomDao {

    @Query("SELECT * FROM fee_marketing WHERE kavling_kode=:kavlingKode")
    fun getByKavlingKode(kavlingKode: String): FeeMarketingRoomEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFeeMarketing(entity: FeeMarketingRoomEntity): Long

    @Query("UPDATE fee_marketing SET " +
            "timeMillis=:newTimeMillis, nama_marketer=:newNamaMarketer, biaya_marketer=:newBiayaMarketer " +
            "WHERE kavling_kode=:kavlingKode")
    fun updateFeeMarketing(kavlingKode: String, newNamaMarketer: String, newBiayaMarketer: Long, newTimeMillis: Long)

    @Query("DELETE FROM fee_marketing WHERE kavling_kode=:kavlingKode")
    fun deleteFeeMarketing(kavlingKode: String)
}