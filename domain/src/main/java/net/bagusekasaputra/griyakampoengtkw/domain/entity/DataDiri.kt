package net.bagusekasaputra.griyakampoengtkw.domain.entity

data class DataDiri(
    val nama: String,
    val jenisIdentitas: String,
    val noIdentitas: String,
    val negaraBekerja: String,
    val alamatKerja: String,
    val alamatIndo: String,
    val noHp: String
) {

    companion object {
        fun EMPTY(): DataDiri {
            return DataDiri(
                nama = "N/A",
                jenisIdentitas = "KTP",
                noIdentitas = "0000",
                negaraBekerja = "N/A",
                alamatKerja = "N/A",
                alamatIndo = "N/A",
                noHp = "0000",
            )
        }
    }
}