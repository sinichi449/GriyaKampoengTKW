package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.DatabaseUserModel

interface RemoteDatabaseUserDataSource {
    
    suspend fun getAll(): Result<DatabaseUserModel?>
    
    suspend fun insert(model: DatabaseUserModel): Result<Nothing?>
    
}