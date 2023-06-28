package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.UpahPekerja
import net.bagusekasaputra.griyakampoengtkw.domain.repository.UpahPekerjaRepository

class UpahPekerjaRepositoryImpl: UpahPekerjaRepository {

    override fun getAll(kavling: String, dataMode: DataMode): Flow<Result<List<UpahPekerja>?>> {
        return flow {
            val upahPekerjaList = mutableListOf<UpahPekerja>()

            upahPekerjaList.add(UpahPekerja(
                untukKavling = "D1",
                tanggalDibayarkan = "24/06/2023".toDate(),
                mingguKe = 1,
                mandor = "Pak Sugeng",
                progress = 16.0,
                jumlahDibayarkan = 3_000_000L,
            ))
            upahPekerjaList.add(
                UpahPekerja(
                    untukKavling = "D1",
                    tanggalDibayarkan = "01/07/2023".toDate(),
                    mingguKe = 2,
                    mandor = "Pak Pandri",
                    progress = 20.74,
                    jumlahDibayarkan = 790_000L,
                )
            )
            upahPekerjaList.add(UpahPekerja(
                untukKavling = "D1",
                tanggalDibayarkan = "08/07/2023".toDate(),
                mingguKe = 3,
                mandor = "Pak Blablabla",
                progress = 24.0,
                jumlahDibayarkan = 4_250_000L,
                kuitansiUri = "https://www.google.com"
            ))

            emit(Result.success(upahPekerjaList))
        }
    }

}