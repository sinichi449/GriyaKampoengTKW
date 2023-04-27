package net.bagusekasaputra.griyakampoengtkw.presentation.network

sealed class NetworkStatus {
    object Available: NetworkStatus()
    object Unavailable: NetworkStatus()
}