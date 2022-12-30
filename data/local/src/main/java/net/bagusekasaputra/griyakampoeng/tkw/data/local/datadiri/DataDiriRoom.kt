package net.bagusekasaputra.griyakampoeng.tkw.data.local.datadiri

import androidx.room.*

@Entity(
    tableName = "data_diri",
    indices = [Index(value = ["kavling_kode"], unique = true)]
)
data class DataDiriRoomEntity(
    @PrimaryKey
    var id: Long? = null,
    @ColumnInfo(name = "kavling_kode")
    val kavlingKode: String,
    @ColumnInfo(name = "nama")
    var nama: String,
    @ColumnInfo(name = "jenis_identitas")
    var jenisIdentitas: String,
    @ColumnInfo(name = "no_identitas")
    var noIdentitas: String?,
    @ColumnInfo(name = "negara_bekerja")
    var negaraBekerja: String,
    @ColumnInfo(name = "alamat_kerja")
    var alamatKerja: String?,
    @ColumnInfo(name = "alamat_indo")
    var alamatIndo: String?,
    @ColumnInfo(name = "no_hp")
    var noHp: String?,
)


@Dao
interface DataDiriRoomDao {

    @Query("SELECT * FROM data_diri WHERE kavling_kode=:kavlingKode")
    fun getByKavlingKode(kavlingKode: String): DataDiriRoomEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dataDiriRoomEntity: DataDiriRoomEntity): Long

    @Query("UPDATE data_diri SET nama=:nama, jenis_identitas=:jenisIdentitas, " +
            "no_identitas=:noIdentitas, negara_bekerja=:negaraBekerja, " +
            "alamat_kerja=:alamatKerja, alamat_indo=:alamatIndo, no_hp=:noHp " +
            "WHERE kavling_kode=:kavlingKode")
    fun updateDataDiri(
        kavlingKode: String,
        nama: String,
        jenisIdentitas: String,
        noIdentitas: String?,
        negaraBekerja: String,
        alamatKerja: String?,
        alamatIndo: String?,
        noHp: String?
    ): Int

    @Query("DELETE FROM data_diri WHERE kavling_kode=:kavlingKode")
    fun deleteByKavlingKode(kavlingKode: String)

    @Query("DELETE FROM data_diri")
    fun deleteAll()
}