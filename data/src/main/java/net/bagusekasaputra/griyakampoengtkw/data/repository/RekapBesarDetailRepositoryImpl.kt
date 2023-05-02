package net.bagusekasaputra.griyakampoengtkw.data.repository

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.bagusekasaputra.griyakampoengtkw.domain.entity.rekap.RekapBesarDetail
import net.bagusekasaputra.griyakampoengtkw.domain.repository.RekapBesarDetailRepository
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter

/**
 * A special Repository Implementation that use JSON to persisting data
 * and DOES NOT need of any Data Source
 */
class RekapBesarDetailRepositoryImpl(
    internalFile: File,
): RekapBesarDetailRepository {

    private val jsonFile = File(internalFile, "rekap_besar_detail.json")

    override suspend fun get(): Result<RekapBesarDetail?> {
        return try {
            val fileReader = FileReader(jsonFile)
            val bufferedReader = BufferedReader(fileReader)
            val stringBuilder = StringBuilder()

            // Read lines
            var line = bufferedReader.readLine()
            while (line != null) {
                stringBuilder.append(line).append("\n")
                line = bufferedReader.readLine()
            }

            bufferedReader.close()

            // Convert to RekapBesarDetail object
            val jsonString = stringBuilder.toString()
            val rekapBesarDetail = Gson().fromJson(jsonString, RekapBesarDetail::class.java)

            Result.success(rekapBesarDetail)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun insert(rekapBesarDetail: RekapBesarDetail): Result<Nothing?> {
        return try {
            // Convert to JSON
            val gson = GsonBuilder().setPrettyPrinting().create()
            val rekapBesarDetailJson = gson.toJson(rekapBesarDetail)

            // Save to file
            val fileWriter = FileWriter(jsonFile)
            val bufferedWriter = BufferedWriter(fileWriter)
            bufferedWriter.write(rekapBesarDetailJson)
            bufferedWriter.close()

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }

    override suspend fun delete(): Result<Nothing?> {
        return try {
            if (jsonFile.exists()) jsonFile.delete()

            Result.success(null)
        } catch (e: Exception) {
            e.printStackTrace()

            Result.failure(e)
        }
    }
}