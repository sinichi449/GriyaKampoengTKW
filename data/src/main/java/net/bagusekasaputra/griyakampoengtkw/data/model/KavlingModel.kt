package net.bagusekasaputra.griyakampoengtkw.data.model

import net.bagusekasaputra.griyakampoengtkw.domain.entity.Kavling

class KavlingModel(
    val kode: String = "",
    val warna: String = "",
    // is active means that there are no costumer here
    val active: Boolean = true,
    val ukuran: String = "",
    val type: String = "",
) {

    companion object {
        fun getBlockKode(kavlingKode: String): String {
            val kavling = Kavling(kavlingKode, false, "", "", "", false)

            return kavling.blockKode
        }

    }
}