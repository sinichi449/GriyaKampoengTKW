package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.data.model.FotoPembayaranModel

interface RemoteFotoPembayaranDataSource {

    suspend fun get(kavlingKode: String, termin: String): FotoPembayaranModel?

    fun isFotoPembayaranExist(kavlingKode: String, termin: String): Flow<Result<Boolean>>

}