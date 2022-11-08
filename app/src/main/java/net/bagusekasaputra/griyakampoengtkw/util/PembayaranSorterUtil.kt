package net.bagusekasaputra.griyakampoengtkw.util

import net.bagusekasaputra.griyakampoengtkw.domain.entity.JenisPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran

class PembayaranSorterUtil(private val pembayaranList: List<Pembayaran>) {

    fun getSortedList(): List<Pembayaran> {
        return sortListPembayaran(pembayaranList)
    }

    private fun sortListPembayaran(listPembayaran: List<Pembayaran>): List<Pembayaran> {
        val itjGroup = createGroupPembayaran(JenisPembayaran.ITJ, listPembayaran)
        val dpGroup = createGroupPembayaran(JenisPembayaran.DP, listPembayaran)
        val terminGroup = createGroupPembayaran(JenisPembayaran.TERMIN, listPembayaran)

        val orderedPembayaran = ArrayList<Pembayaran>()
        itjGroup.listPembayaran.forEach { orderedPembayaran.add(it) }
        dpGroup.listPembayaran.forEach { orderedPembayaran.add(it) }
        terminGroup.listPembayaran.forEach { orderedPembayaran.add(it) }

        return orderedPembayaran
    }

    private fun createGroupPembayaran(jenisPembayaran: String, listPembayaran: List<Pembayaran>): GroupPembayaran {
        val filteredListPembayaran = listPembayaran.filter { pembayaran ->
            val termin = pisahkanTerminDanUrutan(pembayaran.termin)

            return@filter termin[Komponen.JENIS]!! == jenisPembayaran
        }

        return GroupPembayaran(jenisPembayaran, filteredListPembayaran)
    }

    private companion object {
        fun pisahkanTerminDanUrutan(termin: String): Map<String, String> {
            val terminDanUrutan = termin.split(" ")
            return mapOf<String, String>(
                Pair(Komponen.JENIS, terminDanUrutan[0]),
                Pair(Komponen.URUTAN, terminDanUrutan[1]),
            )
        }
    }

    private object Komponen {
        const val JENIS = "jenis"
        const val URUTAN = "urutan"
    }

    private data class GroupPembayaran(val jenisPembayaran: String, var listPembayaran: List<Pembayaran>) {
        init { sort() }

        private fun sort() {
            this.listPembayaran = listPembayaran.sortedBy {
                val termin = pisahkanTerminDanUrutan(it.termin)

                return@sortedBy termin[Komponen.URUTAN]!!
            }
        }
    }



//    fun createRandomListPembayaran(): List<Pembayaran> {
//        return mutableListOf<Pembayaran>(
//            Pembayaran("ITJ 1"),
//            Pembayaran("ITJ 2"),
//            Pembayaran("ITJ 3"),
//            Pembayaran("DP 1"),
//            Pembayaran("DP 2"),
//            Pembayaran("DP 3"),
//            Pembayaran("Termin 1"),
//            Pembayaran("Termin 2"),
//            Pembayaran("Termin 3"),
//            Pembayaran("Termin 4"),
//            Pembayaran("Termin 5"),
//        ).shuffled()
//    }

}