package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.kavling

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.KavlingAndNama
import net.bagusekasaputra.griyakampoengtkw.domain.repository.DataDiriRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository

class GetKavlingAndNamaAsyncUseCase(
    private val kavlingRepository: KavlingRepository,
    private val dataDiriRepository: DataDiriRepository,
): AsyncUseCase<GetKavlingAndNamaAsyncUseCase.Request, List<KavlingAndNama>?>() {

    data class Request(val blok: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<KavlingAndNama>?>> {
        return flow {
            val kavlingList = kavlingRepository.getKavlingByBlock(request.blok, DataMode.ONLINE)
                .first().getOrThrow()

            val kavlingAndNamaList = mutableListOf<KavlingAndNama>()
            kavlingList?.forEach {
                val dataDiri = dataDiriRepository.getDataDiri(it.kode, DataMode.ONLINE)
                    .first().getOrThrow()

                kavlingAndNamaList.add(
                    KavlingAndNama(
                        namaUser = dataDiri?.nama ?: "-",
                        kavlingKode = it.kode,
                        warna = it.warna
                    )
                )
            }

            emit(Result.success(kavlingAndNamaList))
        }
    }
}