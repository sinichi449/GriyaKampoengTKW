package net.bagusekasaputra.griyakampoengtkw.ui.network

sealed class NetworkStatus {
    object Available: NetworkStatus()
    object Unavailable: NetworkStatus()
}