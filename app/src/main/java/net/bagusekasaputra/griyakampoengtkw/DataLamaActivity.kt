package net.bagusekasaputra.griyakampoengtkw

import abhishekti7.unicorn.filepicker.UnicornFilePicker
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import net.bagusekasaputra.griyakampoengtkw.databinding.ActivityDataLamaBinding
import net.bagusekasaputra.griyakampoengtkw.idk.DefaultDataLamaManager
import net.bagusekasaputra.griyakampoengtkw.idk.IDataLamaManager

@AndroidEntryPoint
class DataLamaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDataLamaBinding

    private val PICK_DATA_LAMA_REQUEST = 2
    private val READ_STORAGE_REQUEST = 3

    private val dataLamaManager: IDataLamaManager by lazy {
        DefaultDataLamaManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataLamaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Pilih Data"

        requestReadExternalStorage()

        dataLamaManager.createDataLamaFolderIfNotExist()
        dataLamaManager.getFiles().let {
            setupRecyclerView(it)
        }

        binding.fabTambahData.setOnClickListener {
            showDataLamaPicker()
        }
    }

    private fun setupRecyclerView(listFiles: List<String>) {
        if (listFiles.isEmpty()) {
            // TODO
        } else {
            val adapter = DataLamaRecyclerAdapter(listFiles) {
                // TODO: On Delete data
            }

            binding.recyclerviewDataLama.adapter = adapter
            binding.recyclerviewDataLama.layoutManager = LinearLayoutManager(this)
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
            .theme(abhishekti7.unicorn.filepicker.R.style.UnicornFilePicker_Default)
            .build()
            .forResult(PICK_DATA_LAMA_REQUEST)
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
                dataLamaManager.extract(files?.get(0))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == READ_STORAGE_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("DEBUG_ME", "Read external storage permission granted.")
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d("DEBUG_ME", "Write external storage permission granted.")
            }
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}