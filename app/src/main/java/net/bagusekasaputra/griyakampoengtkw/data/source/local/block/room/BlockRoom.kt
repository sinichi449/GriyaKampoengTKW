package net.bagusekasaputra.griyakampoengtkw.data.source.local.block.room

import androidx.room.*

@Entity(
    tableName = "blocks",
    indices = [
        Index(value = ["kode"], unique = true)
    ]
)
data class BlockRoomEntity(
    @PrimaryKey
    var id: Long? = null,
    @ColumnInfo(name = "kode")
    val kode: String,
    @ColumnInfo(name = "warna")
    val warna: String,
)


@Dao
interface BlockRoomDao {

    @Query("SELECT * FROM blocks")
    fun getAllBlocks(): List<BlockRoomEntity>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(blockRoom: BlockRoomEntity): Long

}