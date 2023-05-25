package net.bagusekasaputra.griyakampoeng.tkw.data.local.indenBooking

//import androidx.room.Dao
//import androidx.room.Entity
//import androidx.room.Index
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.PrimaryKey
//import androidx.room.Query
//
//@Entity(
//    tableName = "data_diri_inden_booking",
//    indices = [Index(value = ["keyId", "nama"], unique = true)]
//)
//data class DataDiriIndenBookingEntity(
//    @PrimaryKey(autoGenerate = false)
//    val keyId: String,
//    val nama: String,
//    val jenisIdentitas: String,
//    val negaraBekerja: String,
//    val alamatKerja: String,
//    val alamatIndo: String,
//    val noHp: String,
//)
//
//@Dao
//interface DataDiriIndenBookingDao {
//
//    @Query("SELECT * FROM data_diri_inden_booking WHERE keyId=:keyId")
//    fun getByKeyId(keyId: String): DataDiriIndenBookingEntity?
//
//    @Insert(onConflict = OnConflictStrategy.FAIL)
//    fun insert(entity: DataDiriIndenBookingEntity): Long
//
//    @Query("DELETE FROM data_diri_inden_booking")
//    fun deleteAll()
//
//}
