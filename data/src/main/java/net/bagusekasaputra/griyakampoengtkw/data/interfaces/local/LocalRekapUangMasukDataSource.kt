package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.RekapUangMasukModel

interface LocalRekapUangMasukDataSource {

    suspend fun getAll(): Result<List<RekapUangMasukModel>?>

    suspend fun insert(model: RekapUangMasukModel): Result<Nothing?>

    suspend fun clearAll(): Result<Nothing?>
}