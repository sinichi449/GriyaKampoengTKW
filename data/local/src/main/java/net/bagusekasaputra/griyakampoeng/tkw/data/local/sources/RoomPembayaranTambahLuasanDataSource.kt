package net.bagusekasaputra.griyakampoeng.tkw.data.local.sources

import net.bagusekasaputra.griyakampoengtkw.data.interfaces.local.LocalPembayaranTambahLuasanDataSource
import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranTambahLuasanModel

class RoomPembayaranTambahLuasanDataSource: LocalPembayaranTambahLuasanDataSource {
    override fun getAll(): Result<List<PembayaranTambahLuasanModel>?> {
        return Result.success(null)
    }

    override fun insertAll(models: List<PembayaranTambahLuasanModel>): Result<Nothing?> {
        return Result.success(null)
    }

    override fun deleteAll(): Result<Nothing?> {
        return Result.success(null)
    }
}