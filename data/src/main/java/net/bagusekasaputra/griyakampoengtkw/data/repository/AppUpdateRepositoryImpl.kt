package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.DataUtil
import net.bagusekasaputra.griyakampoengtkw.data.MyObjectMapper.mapAppUpdate
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote.RemoteAppUpdateSource
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

}