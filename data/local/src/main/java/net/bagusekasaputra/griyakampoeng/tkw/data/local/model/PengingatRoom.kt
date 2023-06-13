package net.bagusekasaputra.griyakampoeng.tkw.data.local.model

import androidx.room.*

@Entity(tableName = "pengingat")
data class PengingatRoomEntity(
    @PrimaryKey
    var id: Long? = null,
    val title: String,
    val content: String,
    val date: String,
    val time: String,
    @ColumnInfo(name = "is_active")
    var isActive: Boolean,
)


@Dao
interface PengingatRoomDao {

    @Query("SELECT * FROM pengingat")
    fun getAll(): List<PengingatRoomEntity>?

    @Query("SELECT * FROM pengingat WHERE id=:id")
    fun getSingle(id: Long): PengingatRoomEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(pengingatRoomEntity: PengingatRoomEntity): Long

    @Query("UPDATE pengingat SET " +
            "title=:newTitle, content=:newContent, date=:newDate, time=:newTime, is_active=:newIsActive " +
            "WHERE id=:id")
    fun update(id: Long, newTitle: String, newContent: String, newDate: String, newTime: String, newIsActive: Boolean)

    @Query("DELETE FROM pengingat WHERE id=:id")
    fun deleteById(id: Long)

}

