package net.bagusekasaputra.griyakampoengtkw.domain.usecase.appupdate

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.BuildConfig
import net.bagusekasaputra.griyakampoengtkw.domain.entity.AppUpdate
import net.bagusekasaputra.griyakampoengtkw.domain.repository.AppUpdateRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase
import net.bagusekasaputra.griyakampoengtkw.util.GriyaNodes.Companion.LOG_TAG
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetUpdateInformationUseCase @Inject constructor(
    private val appUpdateRepository: AppUpdateRepository
): UseCase<GetUpdateInformationUseCase.Request, GetUpdateInformationUseCase.Response>() {

    object Request: UseCase.Request

    data class Response(val result: Result<AppUpdate?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return appUpdateRepository.getUpdateInformation().map {
            mapResult(it)
        }
    }

    private fun mapResult(result: Result<AppUpdate?>): Response {
        val appUpdate = result.getOrNull()

        return if (appUpdate != null) {
            if (isNewerVersion(appUpdate)) {
                Response(Result.success(appUpdate))
            } else {
                Response(Result.success(null))
            }
        } else {
            Response(Result.failure(result.exceptionOrNull() ?: UnknownError("Terjadi kesalahan mendapatkan update")))
        }
    }

    private fun isNewerVersion(appUpdate: AppUpdate): Boolean {
        val currentVersionName = BuildConfig.VERSION_NAME
        val currentVersionCode = BuildConfig.VERSION_CODE

        Log.d(LOG_TAG, "Version name compare: $currentVersionName -> ${appUpdate.latestVersion}")
        Log.d(LOG_TAG, "Version code compare: $currentVersionCode -> ${appUpdate.latestVersionCode}")

        return (currentVersionName != appUpdate.latestVersion) ||
                (currentVersionCode != appUpdate.latestVersionCode)
    }
}