package net.bagusekasaputra.griyakampoengtkw.data.source.remote.kavling

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.data.model.KavlingModel

interface RemoteKavlingRepository {

    fun getAllKavlings(blockKode: String): Flow<Result<List<KavlingModel>>>

    fun addKavling(blockKode: String, kavlingModel: KavlingModel): Flow<Result<Boolean>>

    fun editKavling(blockKode: String, oldKavling: KavlingModel, newKavling: KavlingModel): Flow<Result<Boolean>>

    fun removeKavling(blockKode: String, kavlingKode: String): Flow<Result<Boolean>>
}