package net.bagusekasaputra.griyakampoengtkw.data.interfaces.remote

import kotlinx.coroutines.flow.Flow
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaKavlingModel

interface RemoteHargaKavlingSource {

    fun getHargaKavlingModel(kavlingKode: String): Flow<Result<HargaKavlingModel?>>

    fun addHargaKavlingModel(hargaKavlingModel: HargaKavlingModel): Flow<Result<Boolean>>

    fun getSingleHargaKavlingForPembayaran(kavlingKode: String, onSuccess: (hargaKavlingModel: HargaKavlingModel?) -> Unit)
}