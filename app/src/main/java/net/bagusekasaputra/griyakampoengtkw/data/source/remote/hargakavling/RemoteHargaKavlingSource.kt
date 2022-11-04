package net.bagusekasaputra.griyakampoengtkw.data.source.remote.hargakavling

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaKavlingModel

interface RemoteHargaKavlingSource {

    fun getHargaKavlingModel(kavlingKode: String): Flow<Result<HargaKavlingModel?>>

    fun addHargaKavlingModel(hargaKavlingModel: HargaKavlingModel): Flow<Result<Boolean>>

}