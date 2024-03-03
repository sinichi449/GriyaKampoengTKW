package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.entity.PembayaranTambahLuasan
import net.bagusekasaputra.griyakampoengtkw.domain.repository.PembayaranTambahLuasanRepository

class PembayaranTambahLuasanRepositoryImpl: PembayaranTambahLuasanRepository {

    override fun getAll(kavling: String): Flow<Result<List<PembayaranTambahLuasan>?>> {
        return flow {
            delay(200L)

            val defaultKavling = "E14"
            if (kavling == defaultKavling) {
                emit(Result.success(
                    listOf(
                        PembayaranTambahLuasan(kavling = defaultKavling, fotoUri = "lorem ipsum", tanggal = "14/07/2023", jumlahUang = 1_000_000L),
                        PembayaranTambahLuasan(kavling = defaultKavling, fotoUri = "", tanggal = "23/09/2023", jumlahUang = 26_000_000L),
                        PembayaranTambahLuasan(kavling = defaultKavling, fotoUri = "", tanggal = "12/12/2023", jumlahUang = 9_000_000L),
                    )
                ))
            } else {
                emit(Result.success(null))
            }
        }
    }

}