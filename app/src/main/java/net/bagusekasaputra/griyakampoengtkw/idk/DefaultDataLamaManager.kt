package net.bagusekasaputra.griyakampoengtkw.idk

import android.content.Context
import android.util.Log
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.progress.ProgressMonitor
import org.apache.commons.io.FilenameUtils
import java.io.File

class DefaultDataLamaManager(
    private val context: Context
): IDataLamaManager {

    private val dataLamaFolder by lazy {
        File(context.filesDir, "data_lama")
    }


    override fun createDataLamaFolderIfNotExist() {
        // If folder isn't exist, create a folder named "data_lama"
        if (dataLamaFolder.exists().not()) {
            dataLamaFolder.mkdir()
        }
    }

    override fun getFiles(): List<String> {
        val listFiles = mutableListOf<String>()

        dataLamaFolder.listFiles()?.forEach {
            listFiles.add(it.name)
        }

        return listFiles
    }

    override fun extract(pathToFile: String?) {
        if (pathToFile != null) {
            val zipFile = ZipFile(pathToFile)
            val progressMonitor = zipFile.progressMonitor

            // Create folder same name as the zip file
            val filename = File(pathToFile).name
            val dataLamaFileNameWithoutExt = FilenameUtils.removeExtension(filename)
            val newFolder = File(dataLamaFolder, dataLamaFileNameWithoutExt)
            newFolder.mkdir()

            zipFile.extractAll("${dataLamaFolder.path}/$dataLamaFileNameWithoutExt")

            if (progressMonitor.result.equals(ProgressMonitor.Result.SUCCESS)) {
                Log.d("DEBUG_ME", "Extraction complete")
            }

        } else {
            // Path invalid dialog
            MaterialAlertDialogBuilder(context)
                .setTitle("Invalid Path")
                .setMessage("File yang dipilih tidak valid.")
                .create()
                .show()
        }
    }

}