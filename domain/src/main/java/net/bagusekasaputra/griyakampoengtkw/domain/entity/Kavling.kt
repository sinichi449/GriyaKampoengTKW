package net.bagusekasaputra.griyakampoengtkw.domain.entity

data class Kavling(
    val kode: String,
    val belumIsi: Boolean = true,
    val warna: String,
    val ukuran: String,
    val type: String,
    var sudahBayarBulanIni: Boolean = false,
) {
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
                Pair("C", 5),
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
    }
}

