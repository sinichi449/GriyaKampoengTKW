package net.bagusekasaputra.griyakampoengtkw.domain.mockRepository

import kotlin.random.Random

object MockUtils {

    fun getRandomDuwitValue(min: Long, max: Long) =
        Random.nextLong(from = min, until = max) * 10_000L
}