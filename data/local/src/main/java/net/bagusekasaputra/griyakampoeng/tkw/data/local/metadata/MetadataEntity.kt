package net.bagusekasaputra.griyakampoeng.tkw.data.local.metadata

import androidx.room.*

@Entity(
    tableName = "tables_metadata",
    indices = [Index(value = ["table_name"], unique = true)]
)
data class MetadataEntity(
    @PrimaryKey
    val id: Long = 0L,
    @ColumnInfo(name = "table_name")
    val tableName: String,
    val timestamp: Long,
)


@Dao
interface MetadataDao {

    @Query("SELECT * FROM tables_metadata WHERE table_name=:tableName")
    fun get(tableName: String): MetadataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: MetadataEntity)


}