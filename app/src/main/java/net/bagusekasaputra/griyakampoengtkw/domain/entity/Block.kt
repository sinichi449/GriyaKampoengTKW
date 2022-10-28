package net.bagusekasaputra.griyakampoengtkw.domain.entity

import net.bagusekasaputra.griyakampoengtkw.R

data class Block(
    val kode: String,
    val warna: Int = getWarnaByKode(kode)
) {

    companion object {
        fun getWarnaByKode(kode: String) =
            when (kode) {
                "A" -> R.color.abang
                "B" -> R.color.oren_1
                "C" -> R.color.oren_2
                "D" -> R.color.black
                else -> R.color.purple_500
            }
    }

}