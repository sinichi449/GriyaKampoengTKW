package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.R

data class Kavling(
    val kode: String,
    val isActive: Boolean = true,
    val warna: Int = getWarnaByKode(kode)
) {
    companion object {
        fun getWarnaByKode(kode: String) =
            when (kode) {
                "A" -> R.color.abang
                "B" -> R.color.oren_1
                "C" -> R.color.oren_2
                "D" -> R.color.purple_500
                else -> R.color.black
            }
    }
}

