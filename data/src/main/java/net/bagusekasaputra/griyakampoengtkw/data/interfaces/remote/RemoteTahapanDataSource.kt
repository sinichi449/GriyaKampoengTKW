package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.TahapanModel

interface RemoteTahapanDataSource{

    suspend fun getAll(): Result<List<TahapanModel>?>

}