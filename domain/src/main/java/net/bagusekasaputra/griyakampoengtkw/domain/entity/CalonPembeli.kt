package net.bagusekasaputra.griyakampoengtkw.domain.entity

data class CalonPembeli(
    var id: Long = 0L,
    val nama: String,
    val noHp: String = "",
    val usernameTiktok: String = "",
    val keterangan: String = "",
    var lastModified: Long,
) {

    companion object {
        fun sortbyLastModified(listCalonPembeli: List<CalonPembeli>?): List<CalonPembeli> {
            return listCalonPembeli?.sortedBy {
                it.lastModified
            } ?: emptyList()
        }
    }
}
