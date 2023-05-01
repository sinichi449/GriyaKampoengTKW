package net.bagusekasaputra.griyakampoengtkw.domain

import net.bagusekasaputra.griyakampoengtkw.domain.entity.JenisPembayaran
import net.bagusekasaputra.griyakampoengtkw.domain.entity.Pembayaran

/**
 * This class contains a full-fledged made-by-me algorithm to sort pembayaran according to ITJ, DP, and Termin order.
 *
 * Briefly, a "Pembayaran" object have a "Jenis" and an "Urutan" components. For example,
 * a "Pembayaran DP 5" has a "jenis" of "DP" and "5" of urutan components. In this algorithm, we need to sort
 * both of these components via two differents subroutines: Grouping the "Jenis" and Ordering the "Urutan".
 *
 * Given set of pembayaran, represented a List<Pembayaran>, we need to group them into "Jenis Pembayaran".
 * The result are three groups of List<Pembayaran> -> ITJ Group, DP Group, and Termin Group.
 *
 * After we group the pembayaran, we need to further sort each group according to their numerical order.
 */
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

                return@sortedBy termin[Komponen.URUTAN]!!.toInt()
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