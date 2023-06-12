package net.bagusekasaputra.griyakampoeng.tkw.data.local.backupRestore

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(
    tableName = "backupRestore",
    indices = [Index(value = ["backupName"])]
)
data class BackupRestoreEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    val backupName: String,
)


@Dao
interface BackupRestoreDao {

    @Query("SELECT * FROM backupRestore WHERE backupName=:backupName")
    fun get(backupName: String): BackupRestoreEntity?

    @Query("SELECT * FROM backupRestore")
    fun getAll(): List<BackupRestoreEntity>?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertAll(entities: List<BackupRestoreEntity>): List<Long>

    @Query("DELETE FROM backupRestore WHERE backupName=:backupName")
    fun delete(backupName: String)

    @Query("DELETE FROM backupRestore")
    fun deleteAll()

}

/**
 * Mapper
 */
fun List<BackupRestoreEntity>.toStringList(): List<String> {
    return buildList {
        this@toStringList.forEach { entity ->
            add(entity.backupName)
        }
    }
}

fun List<String>.toBackupRestoreEntites(): List<BackupRestoreEntity> {
    return buildList {
        this@toBackupRestoreEntites.forEach { name ->
            add(BackupRestoreEntity(backupName = name))
        }
    }
}