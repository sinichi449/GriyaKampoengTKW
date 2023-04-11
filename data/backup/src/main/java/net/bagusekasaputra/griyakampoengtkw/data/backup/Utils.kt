package net.bagusekasaputra.griyakampoengtkw.data.backup

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.*

const val PREFS_PATH_DATA_LAMA = "dataLamaPath"
const val JSON_BLOKS = "bloks.json"
const val JSON_KAVLINGS = "kavlings.json"
const val JSON_PEMBAYARANS = "pembayarans.json"
const val JSON_DATA_DIRI = "data_diri.json"
const val JSON_HARGA_KAVLING = "harga_kavling.json"
const val JSON_CATATAN_PEMBAYARAN = "catatan_pembayaran.json"
const val JSON_BIAYA_MARKETINGS = "biaya_marketings.json"
const val JSON_FEE_MARKETING = "fee_marketing.json"

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

fun <O> getGsonJsonString(obj: O): String {
    val gson = GsonBuilder().setPrettyPrinting().create()

    return gson.toJson(obj)
}