package net.bagusekasaputra.griyakampoeng.tkw.data.local.block

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
    var warna: String,
)


@Dao
interface BlockRoomDao {

    @Query("SELECT * FROM blocks")
    fun getAllBlocks(): List<BlockRoomEntity>?

    @Query("SELECT * FROM blocks WHERE kode=:kode")
    fun getSingleBlock(kode: String): BlockRoomEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(blockRoom: BlockRoomEntity): Long

}