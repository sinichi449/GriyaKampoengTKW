package net.bagusekasaputra.griyakampoengtkw.domain.entity

data class DatabaseUser(
    var id: Long = 0L,
    val nama: String,
    val noHp: String = "",
    val usernameTiktok: String = "",
    val keterangan: String = "",
    var lastModified: Long,
) {

    companion object {
        fun sortbyLastModified(listDatabaseUser: List<DatabaseUser>?): List<DatabaseUser> {
            return listDatabaseUser?.sortedBy {
                it.lastModified
            } ?: emptyList()
        }
    }
}
