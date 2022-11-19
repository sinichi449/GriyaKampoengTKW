package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import net.bagusekasaputra.griyakampoengtkw.data.model.AppUpdateModel

interface RemoteAppUpdateSource {

    suspend fun getUpdateInformation(): Result<AppUpdateModel?>

}