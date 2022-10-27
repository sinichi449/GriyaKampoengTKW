package net.bagusekasaputra.griyakampung.data.source.local

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class LocalKavlingRepositoryImpl @Inject constructor(

): LocalKavlingRepository {

    private var kavlings = HashMap<String, ArrayList<KavlingModel>>()

    private fun generatePseudoKavlings(kode: String) {
        val limit = Random.nextInt(8, 30)
        val tempKavlings = ArrayList<KavlingModel>()

        for (i in 1 .. limit) {
            tempKavlings.add(
                KavlingModel("$kode$i", true)
            )
        }

        kavlings[kode] = tempKavlings
    }

    override fun getKavlingByKode(kode: String): List<KavlingModel> {
        generatePseudoKavlings(kode)
        return kavlings[kode]!!
    }
}