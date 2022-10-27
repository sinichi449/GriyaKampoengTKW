package net.bagusekasaputra.griyakampung.data.source.local

interface LocalKavlingRepository {

    fun getKavlingByKode(kode: String): List<KavlingModel>
}