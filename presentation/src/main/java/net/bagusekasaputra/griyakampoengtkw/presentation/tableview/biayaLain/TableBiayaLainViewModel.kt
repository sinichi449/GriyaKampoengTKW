package net.bagusekasaputra.griyakampoengtkw.presentation.tableview.biayaLain

import net.bagusekasaputra.griyakampoengtkw.domain.entity.BiayaLain

data class BlColumnHeader(
    val text: String,
) {
    companion object {
        fun getColumnHeaders(): List<BlColumnHeader> {
            return listOf(
                BlColumnHeader("Jenis Biaya"),
                BlColumnHeader("Harga"),
                BlColumnHeader("Tanggal"),
            )
        }
    }
}

data class BlRowHeader(
    val nomor: String,
) {
    companion object {
        fun getRowHeaders(listSize: Int): List<BlRowHeader> {
            return if (listSize > 0) {
                val rowHeaders = mutableListOf<BlRowHeader>()
                (1..listSize).forEach {
                    rowHeaders.add(BlRowHeader(it.toString()))
                }

                rowHeaders
            } else {
                listOf(
                    BlRowHeader("0")
                )
            }
        }
    }
}

data class BlCell(
    val text: String,
) {
    companion object {
        fun getListCellItems(listBiayaLain: List<BiayaLain>): List<List<BlCell>> {
            val firstList = mutableListOf<List<BlCell>>()

            listBiayaLain.forEach {
                val secondList = mutableListOf<BlCell>().apply {
                    add(BlCell(it.jenisBiaya))
                    add(BlCell(it.parsedHarga))
                    add(BlCell(it.tanggal))
                }

                firstList.add(secondList)
            }

            return firstList
        }
    }
}