package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.model.HargaKavlingModel
import net.bagusekasaputra.griyakampoengtkw.data.source.remote.hargakavling.RemoteHargaKavlingSource
import net.bagusekasaputra.griyakampoengtkw.domain.entity.HargaKavling
import net.bagusekasaputra.griyakampoengtkw.domain.repository.HargaKavlingRepository
import net.bagusekasaputra.griyakampoengtkw.util.NumberUtil
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HargaKavlingRepositoryImpl @Inject constructor(
    private val remoteHargaKavlingSource: RemoteHargaKavlingSource
): HargaKavlingRepository {

    override fun getHargaKavling(kavlingKode: String): Flow<Result<HargaKavling?>> {
        return flow {
            remoteHargaKavlingSource.getHargaKavlingModel(kavlingKode).collect { result ->
                if (result.isSuccess) {
                    val hargaKavlingModel = result.getOrNull()

                    if (hargaKavlingModel != null) emit(Result.success(mapHargaKavling(hargaKavlingModel)))
                    else emit(Result.success(null))
                } else {
                    result.exceptionOrNull()?.let { throwable ->
                        emit(Result.failure(throwable))
                    }
                }
            }
        }
    }

    override fun addHargaKavling(hargaKavling: HargaKavling): Flow<Result<Boolean>> {
        return flow {
            emitAll(remoteHargaKavlingSource.addHargaKavlingModel(
                mapHargaKavling(hargaKavling)))
        }
    }

    private fun mapHargaKavling(hargaKavling: HargaKavling): HargaKavlingModel {
        return hargaKavling.let {
            HargaKavlingModel(
                kavlingKode = it.kavlingKode,
                harga = NumberUtil.formatStringToLong(it.harga)
            )
        }
    }

    private fun mapHargaKavling(hargaKavlingModel: HargaKavlingModel): HargaKavling {
        return hargaKavlingModel.let {
            HargaKavling(
                kavlingKode = it.kavlingKode,
                harga = NumberUtil.formatLongToString(it.harga)
            )
        }
    }
}