package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import net.bagusekasaputra.griyakampoengtkw.data.model.AppUpdateModel
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.appupdate.RemoteAppUpdateSource
import net.bagusekasaputra.griyakampoengtkw.domain.entity.AppUpdate
import net.bagusekasaputra.griyakampoengtkw.domain.repository.AppUpdateRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppUpdateRepositoryImpl @Inject constructor(
    private val remoteAppUpdateSource: RemoteAppUpdateSource
): AppUpdateRepository {

    override fun getUpdateInformation(): Flow<Result<AppUpdate>> {
        return callbackFlow {
            remoteAppUpdateSource.getUpdateInformation(
                onSuccess = { trySendBlocking(Result.success(mapAppUpdate(it))) },
                onFailure = { trySendBlocking(Result.failure(it)) }
            )

            awaitClose {  }
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