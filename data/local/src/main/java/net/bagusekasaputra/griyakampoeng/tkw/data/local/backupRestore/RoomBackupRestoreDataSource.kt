package net.bagusekasaputra.griyakampoeng.tkw.data.local.backupRestore

import android.util.Log
import net.bagusekasaputra.griyakampoeng.tkw.data.local.MyRoomDatabase
import net.bagusekasaputra.griyakampoeng.tkw.data.local.RoomRequestHelper.roomOperation
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalBackupRestoreDataSource

class RoomBackupRestoreDataSource(
    myRoomDatabase: MyRoomDatabase,
): LocalBackupRestoreDataSource {

    private val LOG_TAG = "BACKUP_RESTORE"

    private val dao by lazy {
        myRoomDatabase.getBackupRestoreDao()
    }

    override suspend fun getListBackups(): Result<List<String>?> {
        return roomOperation {
            val entities = dao.getAll()
            if (entities.isNullOrEmpty()) {
                Log.d(LOG_TAG, "Local data source is NULL or empty!")
            } else {
                entities.forEach { Log.d(LOG_TAG, "Found ${it.backupName} on Local Data Source!") }
            }

            entities?.toStringList()
        }
    }

    override suspend fun insertAll(items: List<String>): Result<Unit> {
        return roomOperation {
            // Delete first
            items.forEach { backupName ->
                if(dao.isExist(backupName)) {
                    Log.d(LOG_TAG, "Attempt to insert already exist \"$backupName\" ! Deleting it first ...")
                    dao.delete(backupName)
                }
            }

            //  Then insert
            dao.insertAll(items.toBackupRestoreEntites())

            Log.d("LOG_TAG", "Completed inserting $items !")
        }
    }

    override suspend fun invalidate(): Result<Unit> {
        return roomOperation {
            Log.d(LOG_TAG, "Invalidation request started ...")

            dao.deleteAll()
        }
    }

    override fun getTableName(): String {
        return "backupRestore"
    }

    private fun BackupRestoreDao.isExist(name: String): Boolean {
        return dao.get(name) != null
    }
}