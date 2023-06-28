package net.bagusekasaputra.griyakampoengtkw.data.model

data class KavlingModel(
    val kode: String = "",
    val warna: String = "",
    // is active means that there are no costumer here
    val active: Boolean = true,
    val ukuran: String = "",
    val type: String = "",
    val isCombined: Boolean = false,
) {

    fun getListKode(): List<String> {
        return if (isCombined) {
            kode.split(" + ")
        } else {
            listOf(kode)
        }
    }

    fun getNumkode(): Int {
        return if (isCombined) {
            val kavlingKodeList = getListKode()

            kavlingKodeList[0].substring(1).toInt()
        } else {
            kode.substring(1).toInt()
        }
    }

    companion object {
        fun getBlockKode(kavlingKode: String): String {
            return kavlingKode.substring(0, 1)
        }
    }
}