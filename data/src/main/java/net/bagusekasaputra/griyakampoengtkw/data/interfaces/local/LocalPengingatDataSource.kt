package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.PengingatModel

interface LocalPengingatDataSource {

    suspend fun getAll(): Result<List<PengingatModel>?>

    suspend fun getSingleById(id: Long): Result<PengingatModel?>

    suspend fun insert(pengingatModel: PengingatModel): Result<Long?>

    suspend fun update(oldPengingat: PengingatModel, newPengingat: PengingatModel): Result<Nothing?>

    suspend fun delete(pengingatModel: PengingatModel): Result<Nothing?>

}