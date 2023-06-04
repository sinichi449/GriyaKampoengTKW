package net.bagusekasaputra.griyakampoengtkw.domain.entity

import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository

data class Kavling(
    val kode: String,
    val belumIsi: Boolean = true,
    val warna: String,
    val ukuran: String,
    val type: String,
    var sudahBayarBulanIni: Boolean = false,
) {
    val blockKode = kode.substring(0, 1)
    val numKode = kode.substring(1).toInt()

    fun getPanjang(): String {
        return ukuran.split("x")[0]
    }

    fun getLebar(): String {
        return ukuran.split("x")[1]
    }

    fun getSudahIsi(): Boolean {
        return !belumIsi
    }

    companion object {

        fun sortKavling(kavlingList: List<Kavling>, sorter: KavlingSorter): List<Kavling> {
            return sorter.sortKavling(kavlingList)
        }

        fun sortKavling(kavlingKodeList: List<String>, sorter: SingleBlockKavlingSorter): List<String> {
            return sorter.sortKodeOnly(kavlingKodeList)
        }

        fun excludeKavlingKode(kavlingKodeList: List<String>, exclusionList: List<String>): List<String> {
            return kavlingKodeList.filter {
                !exclusionList.contains(it)
            }
        }

        fun getKavlingKodes(kavlings: List<Kavling>): List<String> {
            val kavlingStrs = mutableListOf<String>()
            kavlings.forEach { kavling ->
                kavlingStrs.add(kavling.kode)
            }

            return kavlingStrs
        }

        /**
         * "Complete ukuran" is 6x12. For "6" is "panjang", and "12" is "lebar".
         */
        fun getCompleteUkuran(panjang: String, lebar: String): String {
            return "${panjang}x${lebar}"
        }

        /**
         * KavlingKode is the official name for A5.
         * The "A" is blockKode, and "5" is noKavling.
         */
        fun getKavlingKode(blockKode: String, noKavling: String): String {
            return "$blockKode$noKavling"
        }

        fun getGriyaKavlingList(): List<String> {
            val blockWithSum = mapOf(
                Pair("A", 13),
                Pair("B", 4),
                Pair("C", 6),
            )

            val listKavling = mutableListOf<String>()
            blockWithSum.keys.forEach { block ->
                val totalUnit = blockWithSum[block] ?: 0

                (1..totalUnit).forEach { noKavling ->
                    listKavling.add("$block$noKavling")
                }
            }

            return listKavling
        }

        suspend fun fetchKavlingKodesNoDetail(
            dataMode: DataMode,
            blockRepository: BlockRepository,
            kavlingRepository: KavlingRepository,
            rekapExclusion: Boolean
        ): List<String> {
            val kavlingKodeList = mutableListOf<String>()

            val blocks = blockRepository.getAllBlocks(dataMode).first().getOrThrow()
            blocks?.forEach { block ->
                val kavlingList = kavlingRepository.getKavlingByBlock(block.kode, dataMode).first().getOrThrow()

                kavlingList?.also {
                    val sortedKavling = sortKavling(it, SingleBlockKavlingSorter())
                    val sortedKodeKavlingList = getKavlingKodes(sortedKavling)

                    kavlingKodeList.addAll(sortedKodeKavlingList)
                }
            }

            return if (rekapExclusion) {
                val exclusionList = kavlingRepository.getRekapExclusionList().getOrThrow()

                if (exclusionList.isNullOrEmpty()) {
                    kavlingKodeList
                } else {
                    excludeKavlingKode(kavlingKodeList, exclusionList)
                }
            } else {
                kavlingKodeList
            }
        }
    }
}

interface KavlingSorter {
    fun sortKavling(kavlingList: List<Kavling>): List<Kavling>

    fun sortKodeOnly(kavlingKodeList: List<String>): List<String>

}

class SingleBlockKavlingSorter: KavlingSorter {
    override fun sortKavling(kavlingList: List<Kavling>): List<Kavling> {
        val copyKavlingList = kavlingList.toMutableList()

        return copyKavlingList.sortedBy {
            it.numKode
        }
    }

    override fun sortKodeOnly(kavlingKodeList: List<String>): List<String> {
        val kavlingList = mutableListOf<Kavling>().apply {
            kavlingKodeList.forEach { kode ->
                add(Kavling(kode = kode, warna = "", ukuran = "", type = ""))
            }
        }

        val sortedKavlingList = sortKavling(kavlingList)

        return Kavling.getKavlingKodes(sortedKavlingList)
    }

}

