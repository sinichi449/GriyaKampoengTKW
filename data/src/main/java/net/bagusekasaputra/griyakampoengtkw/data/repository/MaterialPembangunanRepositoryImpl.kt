package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.DateUtil.toDate
import net.bagusekasaputra.griyakampoengtkw.domain.entity.pembangunan.MaterialPembangunan
import net.bagusekasaputra.griyakampoengtkw.domain.repository.MaterialPembangunanRepository

class MaterialPembangunanRepositoryImpl: MaterialPembangunanRepository {

    override fun getAll(
        kavling: String,
        dataMode: DataMode
    ): Flow<Result<List<MaterialPembangunan>?>> {
        return flow {
            val materialList = mutableListOf<MaterialPembangunan>()

            materialList.add(MaterialPembangunan(
                untukKavling = "D1",
                namaMaterial = "Besi SNI 10mm",
                tanggal = "28/06/2023".toDate(),
                qty = 40.0,
                satuan = "ljr",
                hargaTotal = 1_320_000L,
                kedatangan = MaterialPembangunan.Kedatangan.Datang(40.0),
            ))
            materialList.add(MaterialPembangunan(
                untukKavling = "D1",
                namaMaterial = "Pasir Cor",
                tanggal = "28/06/2023".toDate(),
                qty = 2.0,
                satuan = "rit",
                hargaTotal = 3_550_000L,
                kedatangan = MaterialPembangunan.Kedatangan.Datang(2.0),
            ))
            materialList.add(MaterialPembangunan(
                untukKavling = "D1",
                namaMaterial = "Batu Bata",
                tanggal = "28/06/2023".toDate(),
                qty = 6000.0,
                satuan = "biji",
                hargaTotal = 2_345_000L,
                kedatangan = MaterialPembangunan.Kedatangan.Datang(6000.0),
            ))


            emit(Result.success(materialList))
        }
    }

    override suspend fun insert(
        kavling: String,
        materialPembangunan: MaterialPembangunan
    ): Result<Unit> {
        delay(3000L)

        return Result.failure(NotImplementedError("Not yet implemented"))
    }

    override suspend fun delete(kavling: String, keyId: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun update(
        kavling: String,
        keyId: String,
        newData: MaterialPembangunan
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

}