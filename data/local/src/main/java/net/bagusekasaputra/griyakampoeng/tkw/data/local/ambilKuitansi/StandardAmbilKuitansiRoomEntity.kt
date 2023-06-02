package net.bagusekasaputra.griyakampoeng.tkw.data.local.ambilKuitansi

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import net.bagusekasaputra.griyakampoengtkw.data.model.StandardAmbilKuitansiModel

@Entity(tableName = "ambil_kuitansi")
data class StandardAmbilKuitansiRoomEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val kavling: String,
    val termin: String,
    val sudahAmbil: Boolean,
)


@Dao
interface StandardAmbilKuitansiDao {

    @Query("SELECT * FROM ambil_kuitansi WHERE kavling=:kavling AND termin=:termin")
    fun get(kavling: String, termin: String): StandardAmbilKuitansiRoomEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: StandardAmbilKuitansiRoomEntity): Long

    @Query("DELETE FROM ambil_kuitansi WHERE kavling=:kavling AND termin=:termin")
    fun delete(kavling: String, termin: String)

    @Query("DELETE FROM ambil_kuitansi")
    fun deleteAll()

}

// Mapper
fun StandardAmbilKuitansiRoomEntity.toModel(): StandardAmbilKuitansiModel {
    return this.let {
        StandardAmbilKuitansiModel(
            kavling = it.kavling,
            termin = it.termin,
            sudahAmbil = it.sudahAmbil,
        )
    }
}

fun StandardAmbilKuitansiModel.toEntity(): StandardAmbilKuitansiRoomEntity {
    return this.let {
        StandardAmbilKuitansiRoomEntity(
            kavling = it.kavling,
            termin = it.termin,
            sudahAmbil = it.sudahAmbil,
        )
    }
}