package net.bagusekasaputra.griyakampoengtkw.model

import java.util.Locale

data class Tahapan(val nama: String) {

    private val tahapanAndNumber: Pair<String, Int> get() {
        val separateTahapanAndNumber = nama.split("_")

        return separateTahapanAndNumber[0] to separateTahapanAndNumber[1].toInt()
    }

    private val capitalizeNama = tahapanAndNumber.first
        .lowercase()
        .replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault())
            else it.toString()
        }

    private val urutan = tahapanAndNumber.second

    val parsedNama = "$capitalizeNama $urutan"
}