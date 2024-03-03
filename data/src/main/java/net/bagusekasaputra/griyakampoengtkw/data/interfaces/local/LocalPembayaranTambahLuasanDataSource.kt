package net.bagusekasaputra.griyakampoengtkw.data.interfaces.local

import net.bagusekasaputra.griyakampoengtkw.data.model.PembayaranTambahLuasanModel

interface LocalPembayaranTambahLuasanDataSource {

    fun getAll(): Result<List<PembayaranTambahLuasanModel>?>

    fun insertAll(models: List<PembayaranTambahLuasanModel>): Result<Nothing?>

    fun deleteAll(): Result<Nothing?>
}