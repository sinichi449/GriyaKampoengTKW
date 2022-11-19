package net.bagusekasaputra.griyakampoengtkw.data.source.remote.appupdate

import net.bagusekasaputra.griyakampoengtkw.data.model.AppUpdateModel

interface RemoteAppUpdateSource {

    suspend fun getUpdateInformation(): Result<AppUpdateModel?>

}