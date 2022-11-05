package net.bagusekasaputra.griyakampoengtkw.data.model

data class AppUpdateModel(
    val latestVersion: String = "",
    val latestVersionCode: Int = 0,
    val url: String = "",
    val releaseNotes: List<String> = emptyList(),
)