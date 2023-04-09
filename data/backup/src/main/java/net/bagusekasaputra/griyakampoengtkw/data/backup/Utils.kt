package net.bagusekasaputra.griyakampoengtkw.data.backup

import com.google.gson.Gson
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader

const val PREFS_PATH_DATA_LAMA = "dataLamaPath"
const val JSON_BLOKS = "bloks.json"
const val JSON_KAVLINGS = "kavlings.json"

fun readFile(file: File): String {
    val fileReader = FileReader(file)
    val bufferedReader = BufferedReader(fileReader)
    val stringBuilder = StringBuilder()

    var line = bufferedReader.readLine()
    while (line != null) {
        stringBuilder.append(line).append("\n")
        line = bufferedReader.readLine()
    }

    bufferedReader.close()

    return stringBuilder.toString()
}

inline fun <reified M> readJson(file: File): M {
    if (file.exists().not()) {
        throw FileNotFoundException("${file.absolutePath} tidak ditemukan!")
    }

    val jsonString = readFile(file)

    return Gson().fromJson(jsonString, M::class.java)
}