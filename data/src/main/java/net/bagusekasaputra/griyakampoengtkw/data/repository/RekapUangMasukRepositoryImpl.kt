package net.bagusekasaputra.griyakampoengtkw.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalRekapUangMasukDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.RekapUangMasukModel
import net.bagusekasaputra.griyakampoengtkw.domain.entity.RekapUangMasuk
import net.bagusekasaputra.griyakampoengtkw.domain.repository.RekapUangMasukRepository

class RekapUangMasukRepositoryImpl(
    private val localRekap: LocalRekapUangMasukDataSource
): RekapUangMasukRepository {

    override fun getAll(): Flow<Result<List<RekapUangMasuk>?>> {
        return flow {
            emit(localRekap.getAll().map { listRekap ->
                    listRekap?.map {
                        mapRekapUangMasuk(it)
                    }
                })
        }
    }

    override fun insert(rekapUangMasuk: RekapUangMasuk): Flow<Result<Nothing?>> {
        return flow {
            emit(localRekap.insert(mapRekapUangMasuk(rekapUangMasuk)))
        }
    }

    override fun clearAll(): Flow<Result<Nothing?>> {
        return flow {
            emit(localRekap.clearAll())
        }
    }

    private fun mapRekapUangMasuk(model: RekapUangMasukModel): RekapUangMasuk {
        return model.let {
            RekapUangMasuk(
                noKavling = it.noKavling,
                namaCostumer = it.namaCostumer,
                tanggal = it.tanggal,
                jenisPembayaran = it.jenisPembayaran,
                jumlahPembayaran = it.jumlahPembayaran,
            )
        }
    }

    private fun mapRekapUangMasuk(rekapUangMasuk: RekapUangMasuk): RekapUangMasukModel {
        return rekapUangMasuk.let {
            RekapUangMasukModel(
                noKavling = it.noKavling,
                namaCostumer = it.namaCostumer,
                tanggal = it.tanggal,
                jenisPembayaran = it.jenisPembayaran,
                jumlahPembayaran = it.jumlahPembayaran,
            )
        }
    }
}