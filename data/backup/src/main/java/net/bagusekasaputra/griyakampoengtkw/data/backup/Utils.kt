package net.bagusekasaputra.griyakampoengtkw.data.backup

import com.google.gson.Gson
import java.io.*

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

fun writeFile(file: File, str: String) {
    try {
        val fileWriter = FileWriter(file)
        val bufferedWriter = BufferedWriter(fileWriter)

        bufferedWriter.write(str)

        bufferedWriter.close()
    } catch (e: Exception) {
        throw e
    }
}

inline fun <reified M> readJson(file: File): M {
    if (file.exists().not()) {
        throw FileNotFoundException("${file.absolutePath} tidak ditemukan!")
    }

    val jsonString = readFile(file)

    return Gson().fromJson(jsonString, M::class.java)
}