package net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.pembayaranTambahLuasan

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import net.bagusekasaputra.griyakampoengtkw.domain.asyncUseCase.AsyncUseCase
import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaPribadi
import net.bagusekasaputra.griyakampoengtkw.domain.entity.PembayaranTambahLuasan
import net.bagusekasaputra.griyakampoengtkw.domain.repository.FotoTambahLuasanRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranTambahLuasanRepository

class GetAllPembayaranTambahLuasanAsyncUseCase(
    private val dataRepository: PembayaranTambahLuasanRepository,
    private val fotoRepository: FotoTambahLuasanRepository,
): AsyncUseCase<GetAllPembayaranTambahLuasanAsyncUseCase.Request, List<PembayaranTambahLuasan>?>() {

    data class Request(val kavling: String): AsyncUseCase.Request

    override fun process(request: Request): Flow<Result<List<PembayaranTambahLuasan>?>> {
        return flow {
            val tambahLuasanList = dataRepository.getAll(request.kavling)
                .map { result ->
                result.map { data ->
                    data?.let {
                        PembayaranTambahLuasan.sortByTanggal(it)
                    }
                }
            }
                .first()
                .getOrThrow()

            if (tambahLuasanList.isNullOrEmpty()) {
                emit(Result.success(null))
            } else {
                val newListWithFoto = mutableListOf<PembayaranTambahLuasan>()
                // Get foto pembayaran
                tambahLuasanList.forEach { item ->
                    val fotoTambahLuasan = fotoRepository.get(item.kavling, item.id)
                        .first().getOrThrow()
                    newListWithFoto.add(item.copy(fotoUri = fotoTambahLuasan?.uri ?: ""))
                }

                emit(Result.success(newListWithFoto))
            }
        }
    }


}