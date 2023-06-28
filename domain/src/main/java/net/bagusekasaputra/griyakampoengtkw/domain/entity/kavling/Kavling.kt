package net.bagusekasaputra.griyakampoengtkw.domain.entity.kavling

import kotlinx.coroutines.flow.first
import net.bagusekasaputra.griyakampoengtkw.domain.DataMode
import net.bagusekasaputra.griyakampoengtkw.domain.repository.BlockRepository
import net.bagusekasaputra.griyakampoengtkw.domain.repository.KavlingRepository

sealed class Kavling {
    abstract val kode: String
    abstract val belumIsi: Boolean
    abstract val warna: String
    abstract val ukuran: String
    abstract val type: String
    abstract val blockKode: String
    abstract val numKode: Int

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

        fun sortKodeKavling(kavlingKodeList: List<String>, sorter: KavlingSorter): List<String> {
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
            return try {
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

                if (rekapExclusion) {
                    val exclusionList = kavlingRepository.getRekapExclusionList().getOrThrow()

                    if (exclusionList.isNullOrEmpty()) {
                        kavlingKodeList
                    } else {
                        excludeKavlingKode(kavlingKodeList, exclusionList)
                    }
                } else {
                    kavlingKodeList
                }
            } catch (e: Exception) {
                e.printStackTrace()

                throw e
            }
        }
    }
}

data class StandardKavling(
    override val kode: String,
    override val belumIsi: Boolean = true,
    override val warna: String,
    override val ukuran: String,
    override val type: String,
): Kavling() {
    override val blockKode = kode.substring(0, 1)
    override val numKode = kode.substring(1).toInt()

    companion object {
        fun EMPTY(kode: String): StandardKavling {
            return StandardKavling(
                kode = kode,
                belumIsi = true,
                warna = "#000000",
                ukuran = "0x0",
                type = "Type NULL",
            )
        }
    }
}

data class CombinedKavling(
    val kavlingKodeList: List<String>,
    override val belumIsi: Boolean = true,
    override val warna: String,
    override val ukuran: String,
    override val type: String,
    override val numKode: Int,
): Kavling() {

    init {
        if (kavlingKodeList.isEmpty()) {
            throw IllegalArgumentException("Parameter `kavlingKodeList` untuk CombinedKavling tidak boleh kosong!")
        }
    }

    override val kode: String
        get() = buildString {
            kavlingKodeList.forEachIndexed { index, kode ->
                val lastIndex = index == kavlingKodeList.size - 1
                if (lastIndex) {
                    append(kode)
                } else {
                    append("$kode + ")
                }
            }
        }
    override val blockKode: String
        get() = kavlingKodeList[0].substring(0, 1)

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
        return kavlingKodeList
    }

}

