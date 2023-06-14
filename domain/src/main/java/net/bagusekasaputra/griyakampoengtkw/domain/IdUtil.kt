package net.bagusekasaputra.griyakampoengtkw.domain

import java.util.UUID

object IdUtil {

    fun generateKeyId(): String {
        return UUID.randomUUID().toString()
    }
}