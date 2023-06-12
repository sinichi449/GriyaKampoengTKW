package net.bagusekasaputra.griyakampoengtkw.domain

import java.util.UUID

object IdUtil {

    fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }
}