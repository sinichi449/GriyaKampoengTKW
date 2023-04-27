package net.bagusekasaputra.griyakampoengtkw.domain.usecase.appupdate

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.entity.AppUpdate
import net.bagusekasaputra.griyakampoengtkw.domain.repository.AppUpdateRepository
import net.bagusekasaputra.griyakampoengtkw.domain.usecase.UseCase

class GetUpdateInformationUseCase(
    private val appUpdateRepository: AppUpdateRepository
): UseCase<GetUpdateInformationUseCase.Request, GetUpdateInformationUseCase.Response>() {

    data class CurrentBuildConfig(
        val versionName: String,
        val versionCode: Int,
    )

    data class Request(val currentBuildConfig: CurrentBuildConfig): UseCase.Request

    data class Response(val result: Result<AppUpdate?>): UseCase.Response

    override fun process(request: Request): Flow<Response> {
        return appUpdateRepository.getUpdateInformation().map {
            mapResult(it, request.currentBuildConfig)
        }
    }

    private fun mapResult(result: Result<AppUpdate?>, currentBuildConfig: CurrentBuildConfig): Response {
        val appUpdate = result.getOrNull()

        return if (appUpdate != null) {
            if (isNewerVersion(appUpdate, currentBuildConfig)) {
                Response(Result.success(appUpdate))
            } else {
                Response(Result.success(null))
            }
        } else {
            Response(Result.failure(result.exceptionOrNull() ?: UnknownError("Terjadi kesalahan mendapatkan update")))
        }
    }

    private fun isNewerVersion(appUpdate: AppUpdate, currentBuildConfig: CurrentBuildConfig): Boolean {
        val currentVersionName = currentBuildConfig.versionName
        val currentVersionCode = currentBuildConfig.versionCode

        return (currentVersionName != appUpdate.latestVersion) ||
                (currentVersionCode != appUpdate.latestVersionCode)
    }
}