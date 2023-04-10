package net.bagusekasaputra.griyakampoengtkw.data.backup.dataDiri

data class BackupDataDiriModel(
    val kavling: String,
    val dataDiri: DataDiri?,
) {
    data class DataDiri(
        val alamatIndo: String,
        val alamatKerja: String,
        val jenisIdentitas: String,
        val nama: String,
        val negaraBekerja: String,
        val noHp: String,
        val noIdentitas: String,
    )
}