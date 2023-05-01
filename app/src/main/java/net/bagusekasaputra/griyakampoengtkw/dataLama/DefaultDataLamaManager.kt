package net.bagusekasaputra.griyakampoengtkw.dataLama

import android.content.Context
import android.util.Log
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.progress.ProgressMonitor
import org.apache.commons.io.FilenameUtils
import java.io.File

class DefaultDataLamaManager(
    private val context: Context
): AbstractDataLamaManager(context) {

    override fun extract(pathToFile: String?, onFinish: () -> Unit) {
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
                onFinish()
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

    override fun delete(dataLamaModel: DataLamaModel, onFinish: () -> Unit) {
        val folder = File(dataLamaModel.path)
        val deleteOk = folder.deleteRecursively()

        if (deleteOk) {
            onFinish()
        } else {
            Log.d("DEBUG_ME", "Deleting ${dataLamaModel.name} failed")
        }
    }
}