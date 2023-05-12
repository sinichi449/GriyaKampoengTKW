package net.bagusekasaputra.griyakampoengtkw.interfaces.remote

interface InitRemote {

    suspend fun checkMaintenance(): Result<Boolean>


}