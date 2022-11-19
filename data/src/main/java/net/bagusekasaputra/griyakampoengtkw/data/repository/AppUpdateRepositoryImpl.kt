package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.model.AppUpdateModel
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.appupdate.RemoteAppUpdateSource
import net.bagusekasaputra.griyakampoengtkw.domain.entity.AppUpdate
import net.bagusekasaputra.griyakampoengtkw.domain.repository.AppUpdateRepository

class AppUpdateRepositoryImpl(
    private val remoteAppUpdateSource: RemoteAppUpdateSource
): AppUpdateRepository {

    override fun getUpdateInformation(): Flow<Result<AppUpdate?>> {
        return flow {
            val remoteUpdate = remoteAppUpdateSource.getUpdateInformation()
            val mappedRemoteUpdate = DataUtil.mapSingleResult(remoteUpdate, ::mapAppUpdate)

            emit(mappedRemoteUpdate)
        }
    }

    private fun mapAppUpdate(appUpdateModel: AppUpdateModel): AppUpdate {
        return appUpdateModel.let {
            AppUpdate(
                latestVersion = it.latestVersion,
                latestVersionCode = it.latestVersionCode,
                url = it.url,
                releaseNotes = it.releaseNotes,
            )
        }
    }

}