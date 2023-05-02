package net.bagusekasaputra.griyakampoengtkw.dataLama

import android.content.Context
import java.io.File

abstract class AbstractDataLamaManager(private val context: Context) {

    protected val dataLamaFolder by lazy {
        File(context.filesDir, "data_lama")
    }

    fun createDataLamaFolderIfNotExist() {
        // If folder isn't exist, create a folder named "data_lama"
        if (dataLamaFolder.exists().not()) {
            dataLamaFolder.mkdir()
        }
    }

    fun getFiles(): List<DataLamaModel> {
        val listFiles = mutableListOf<DataLamaModel>()

        dataLamaFolder.listFiles()?.forEach {
            listFiles.add(
                DataLamaModel(
                    name = it.name,
                    path = it.absolutePath,
                )
            )
        }

        return listFiles
    }

    abstract fun extract(pathToFile: String?, onFinish: () -> Unit)

    abstract fun delete(dataLamaModel: DataLamaModel, onFinish: () -> Unit)
}