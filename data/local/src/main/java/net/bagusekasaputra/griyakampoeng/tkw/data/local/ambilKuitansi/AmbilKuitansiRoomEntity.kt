package net.bagusekasaputra.griyakampoeng.tkw.data.local.ambilKuitansi

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import net.bagusekasaputra.griyakampoengtkw.data.model.AmbilKuitansiModel

@Entity(tableName = "ambil_kuitansi")
data class AmbilKuitansiRoomEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val kavling: String,
    val termin: String,
    val sudahAmbil: Boolean,
)


@Dao
interface AmbilKuitansiDao {

    @Query("SELECT * FROM ambil_kuitansi WHERE kavling=:kavling AND termin=:termin")
    fun get(kavling: String, termin: String): AmbilKuitansiRoomEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: AmbilKuitansiRoomEntity): Long

    @Query("DELETE FROM ambil_kuitansi")
    fun deleteAll()

}

// Mapper
fun AmbilKuitansiRoomEntity.toModel(): AmbilKuitansiModel {
    return this.let {
        AmbilKuitansiModel(
            kavling = it.kavling,
            termin = it.termin,
            sudahAmbil = it.sudahAmbil,
        )
    }
}

fun AmbilKuitansiModel.toEntity(): AmbilKuitansiRoomEntity {
    return this.let {
        AmbilKuitansiRoomEntity(
            kavling = it.kavling,
            termin = it.termin,
            sudahAmbil = it.sudahAmbil,
        )
    }
}