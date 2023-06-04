package net.bagusekasaputra.griyakampoengtkw.domain.entity

import java.util.Locale

data class Tahapan(val reference: String) {

    private val tahapanAndNumber: Pair<String, Int> get() {
        val separateTahapanAndNumber = reference.split("_")

        return separateTahapanAndNumber[0] to separateTahapanAndNumber[1].toInt()
    }

    private val capitalizeNama = tahapanAndNumber.first
        .lowercase()
        .replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault())
            else it.toString()
        }

    private val urutan = tahapanAndNumber.second

    val nama = "$capitalizeNama $urutan"
}