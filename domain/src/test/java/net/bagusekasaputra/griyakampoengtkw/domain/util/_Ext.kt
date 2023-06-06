package net.bagusekasaputra.griyakampoengtkw.domain.util

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File
import java.io.FileNotFoundException
import java.util.Scanner

private const val TESTING_DATA_FILENAME = "testing_data.json"

fun getTestingFile(testClass: Any, filename: String = TESTING_DATA_FILENAME): File {
    val uri = testClass.javaClass.classLoader?.getResource(filename)?.toURI()!!

    return File(uri)
}

fun readFile(file: File): String {
    try {
        val scanner = Scanner(file)
        val result = StringBuilder()
        while (scanner.hasNext()) {
            val data = scanner.nextLine()
            result.append(data)
        }
        scanner.close()

        return result.toString()
    } catch (e: FileNotFoundException) {
        throw e
    }
}

fun readJson(jsonString: String): JsonObject? {
    return JsonParser.parseString(jsonString)
        .asJsonObject
}

fun File.nodeReference(): JsonObject? {
    val jsonString = readFile(this)

    return readJson(jsonString)
}