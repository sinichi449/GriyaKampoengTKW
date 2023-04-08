package net.bagusekasaputra.griyakampoengtkw

import abhishekti7.unicorn.filepicker.UnicornFilePicker
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.bagusekasaputra.griyakampoengtkw.databinding.ActivityDocumentLamaBinding
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.progress.ProgressMonitor
import org.apache.commons.io.FilenameUtils
import java.io.File

class DocumentLamaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDocumentLamaBinding
    private val dataLamaFolder by lazy {
        File(filesDir, "data_lama")
    }

    private val PICK_DATA_LAMA_REQUEST = 2
    private val READ_STORAGE_REQUEST = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentLamaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Pilih Data"

        // Creating folder "data_lama"
        createDataLamaFolderIfNotExist()

        requestReadExternalStorage()

        binding.fabTambahData.setOnClickListener {
            showDataLamaPicker()
        }
    }

    private fun createDataLamaFolderIfNotExist() {
        // If folder isn't exist, create a folder named "data_lama"
        if (dataLamaFolder.exists().not()) {
            dataLamaFolder.mkdir()
        }
    }

    private fun showDataLamaPicker() {
        UnicornFilePicker.from(this)
            .addConfigBuilder()
            .selectMultipleFiles(false)
            .showOnlyDirectory(false)
            .setRootDirectory(Environment.getExternalStorageDirectory().absolutePath)
            .showHiddenFiles(false)
            .setFilters(arrayOf("zip"))
            .addItemDivider(true)
            .theme(abhishekti7.unicorn.filepicker.R.style.UnicornFilePicker_Dracula)
            .build()
            .forResult(PICK_DATA_LAMA_REQUEST)
    }

    private fun extractDataLamaArchive(pathToFile: String?) {
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
            MaterialAlertDialogBuilder(this)
                .setTitle("Invalid Path")
                .setMessage("File yang dipilih tidak valid.")
                .create()
                .show()
        }
    }

    private fun requestReadExternalStorage() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
            READ_STORAGE_REQUEST,
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            PICK_DATA_LAMA_REQUEST -> if (resultCode == RESULT_OK) {
                val files = data?.getStringArrayListExtra("filePaths")
                extractDataLamaArchive(files?.get(0))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == READ_STORAGE_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("DEBUG_ME", "Read external storage permission granted.")
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}