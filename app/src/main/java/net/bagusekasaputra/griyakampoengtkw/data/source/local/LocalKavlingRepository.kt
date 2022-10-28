package net.bagusekasaputra.griyakampoengtkw.data.source.local

interface LocalKavlingRepository {

    fun getKavlingByKode(kode: String): List<KavlingModel>
}