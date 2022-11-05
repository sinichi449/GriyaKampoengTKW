package net.bagusekasaputra.griyakampoengtkw.domain.entity

data class AppUpdate(
    val latestVersion: String,
    val latestVersionCode: Int,
    val url: String,
    val releaseNotes: List<String>,
)