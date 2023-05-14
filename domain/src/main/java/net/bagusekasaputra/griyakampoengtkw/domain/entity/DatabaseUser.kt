package net.bagusekasaputra.griyakampoengtkw.domain.entity

import java.util.Date

data class DatabaseUser(
    val nama: String,
    val tanggal: Date,
    val noHp: String = "",
    val _usernameTiktok: String = "",
    val lokasiIndo: String,
    val negaraBekerja: String,
    val keterangan: String = "",
    var lastModified: Long = System.currentTimeMillis(),
) {
    val usernameTiktok = "@$_usernameTiktok"

    companion object {
        fun sortbyLastModified(listDatabaseUser: List<DatabaseUser>?): List<DatabaseUser> {
            return listDatabaseUser?.sortedBy {
                it.lastModified
            } ?: emptyList()
        }
    }

}
