package net.bagusekasaputra.griyakampoengtkw.data.backup

import java.io.BufferedReader
import java.io.File
import java.io.FileReader

const val PREFS_PATH_DATA_LAMA = "dataLamaPath"
const val BLOKS_JSON = "bloks.json"

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